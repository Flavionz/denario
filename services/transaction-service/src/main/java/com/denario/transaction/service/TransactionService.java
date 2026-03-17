package com.denario.transaction.service;

import com.denario.transaction.dto.AccountServiceDto;
import com.denario.transaction.dto.TransactionDto.*;
import com.denario.transaction.model.Transaction;
import com.denario.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountServiceClient accountServiceClient;
    private final TransactionEventPublisher eventPublisher;

    @Transactional
    public TransactionResponse transfer(TransferRequest request, String userId) {
        log.info("Processing transfer from {} to {} amount {}",
                request.getSourceIban(), request.getTargetIban(), request.getAmount());

        if (request.getSourceIban().equals(request.getTargetIban())) {
            throw new InvalidTransactionException("Source and target IBAN cannot be the same");
        }

        AccountServiceDto.BalanceResponse balance =
                accountServiceClient.checkBalance(request.getSourceIban(), request.getAmount());

        if (!balance.isSufficient()) {
            throw new InsufficientBalanceException("Insufficient balance on account: " + request.getSourceIban());
        }

        Transaction transaction = Transaction.builder()
                .sourceAccountId(balance.getAccountId())
                .targetAccountId(UUID.randomUUID())
                .sourceIban(request.getSourceIban())
                .targetIban(request.getTargetIban())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .description(request.getDescription())
                .reference(generateReference())
                .initiatedBy(userId)
                .status(Transaction.TransactionStatus.PENDING)
                .build();

        transaction = transactionRepository.save(transaction);

        try {
            accountServiceClient.updateBalance(request.getSourceIban(), request.getAmount().negate());
            accountServiceClient.updateBalance(request.getTargetIban(), request.getAmount());

            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transaction.setCompletedAt(LocalDateTime.now());
            transaction = transactionRepository.save(transaction);

            eventPublisher.publishTransactionCompleted(transaction);
            log.info("Transfer completed: {}", transaction.getReference());

        } catch (Exception e) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setFailureReason(e.getMessage());
            transactionRepository.save(transaction);
            log.error("Transfer failed: {} - {}", transaction.getReference(), e.getMessage());
            throw new TransactionFailedException("Transfer failed: " + e.getMessage());
        }

        return TransactionResponse.from(transaction);
    }

    public PagedTransactionsResponse getMyTransactions(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> result = transactionRepository.findByInitiatedBy(userId, pageable);

        return PagedTransactionsResponse.builder()
                .transactions(result.getContent().stream().map(TransactionResponse::from).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();
    }

    public PagedTransactionsResponse getTransactionsByIban(String iban, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> result = transactionRepository.findByIban(iban, pageable);

        return PagedTransactionsResponse.builder()
                .transactions(result.getContent().stream().map(TransactionResponse::from).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();
    }

    private String generateReference() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%06d", new Random().nextInt(999999));
        String reference = "TXN-" + datePart + "-" + randomPart;
        if (transactionRepository.existsByReference(reference)) {
            return generateReference();
        }
        return reference;
    }

    public static class InvalidTransactionException extends RuntimeException {
        public InvalidTransactionException(String message) { super(message); }
    }

    public static class InsufficientBalanceException extends RuntimeException {
        public InsufficientBalanceException(String message) { super(message); }
    }

    public static class TransactionFailedException extends RuntimeException {
        public TransactionFailedException(String message) { super(message); }
    }

    public static class TransactionNotFoundException extends RuntimeException {
        public TransactionNotFoundException(String message) { super(message); }
    }
}
