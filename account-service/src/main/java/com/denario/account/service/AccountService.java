package com.denario.account.service;

import com.denario.account.dto.AccountDto.*;
import com.denario.account.model.Account;
import com.denario.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Service Layer — contiene tutta la business logic.
 *
 * REGOLA FONDAMENTALE:
 * Il Controller non fa logica → delega al Service.
 * Il Service non sa niente di HTTP → lavora con oggetti Java puri.
 * Il Repository non fa logica → solo accesso al DB.
 *
 * @Transactional: garantisce che le operazioni DB siano atomiche.
 * Se qualcosa va storto a metà, tutto viene rollbackato.
 */
@Service
@RequiredArgsConstructor  // Lombok: genera costruttore con tutti i campi final (= injection)
@Slf4j                    // Lombok: genera logger → log.info(), log.error() ecc.
@Transactional(readOnly = true)  // Default: tutte le operazioni sono read-only (ottimizzazione)
public class AccountService {

    private final AccountRepository accountRepository;
    private final IbanGeneratorService ibanGeneratorService;
    private final AccountEventPublisher eventPublisher;

    /**
     * Recupera tutti i conti dell'utente autenticato.
     * userId viene dal JWT — mai dal body della request!
     */
    public List<AccountSummary> getMyAccounts(String userId) {
        log.debug("Fetching accounts for user: {}", userId);
        return accountRepository.findByUserId(userId)
                .stream()
                .map(AccountSummary::from)
                .toList();  // Java 16+ — più conciso di collect(Collectors.toList())
    }

    /**
     * Recupera un conto specifico verificando che appartenga all'utente.
     */
    public AccountResponse getAccountById(UUID accountId, String userId) {
        Account account = findAccountByIdAndUserId(accountId, userId);
        return AccountResponse.from(account);
    }

    /**
     * Crea un nuovo conto bancario.
     * @Transactional senza readOnly — questa operazione scrive nel DB.
     */
    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request, String userId) {
        log.info("Creating new {} account for user: {}", request.getAccountType(), userId);

        // Genera un IBAN italiano univoco
        String iban = ibanGeneratorService.generateUniqueIban();

        Account account = Account.builder()
                .userId(userId)
                .iban(iban)
                .ownerName(request.getOwnerName())
                .balance(request.getInitialDeposit())
                .currency(request.getCurrency())
                .accountType(request.getAccountType())
                .status(Account.AccountStatus.ACTIVE)
                .build();

        Account saved = accountRepository.save(account);
        log.info("Account created with ID: {} and IBAN: {}", saved.getId(), saved.getIban());

        // Pubblica evento su RabbitMQ — notification-service lo riceverà
        eventPublisher.publishAccountCreated(saved);

        return AccountResponse.from(saved);
    }

    /**
     * Verifica il saldo disponibile — usato da transaction-service.
     * Endpoint interno: non richiede che userId == proprietario del conto target.
     */
    public BalanceResponse checkBalance(String iban, BigDecimal requestedAmount) {
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for IBAN: " + iban));

        boolean sufficient = account.hasSufficientBalance(requestedAmount);

        return BalanceResponse.builder()
                .accountId(account.getId())
                .iban(account.getIban())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .sufficient(sufficient)
                .build();
    }

    /**
     * Aggiorna il saldo — chiamato da transaction-service via evento RabbitMQ.
     * @param iban IBAN del conto
     * @param amount importo positivo = credito, negativo = debito
     */
    @Transactional
    public void updateBalance(String iban, BigDecimal amount) {
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for IBAN: " + iban));

        if (!account.isOperational()) {
            throw new AccountNotOperationalException("Account " + iban + " is not operational");
        }

        if (amount.compareTo(BigDecimal.ZERO) >= 0) {
            account.credit(amount);
        } else {
            // amount è negativo, subtract toglie il segno
            BigDecimal absAmount = amount.abs();
            if (!account.hasSufficientBalance(absAmount)) {
                throw new InsufficientBalanceException("Insufficient balance for account: " + iban);
            }
            account.debit(absAmount);
        }

        accountRepository.save(account);
        log.info("Balance updated for IBAN: {} | amount: {}", iban, amount);
    }

    // ── Helper privato ─────────────────────────────────────────────

    private Account findAccountByIdAndUserId(UUID accountId, String userId) {
        return accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found or access denied: " + accountId));
    }

    // ── Eccezioni custom (inner classes per ora, poi le spostiamo) ──

    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(String message) { super(message); }
    }

    public static class AccountNotOperationalException extends RuntimeException {
        public AccountNotOperationalException(String message) { super(message); }
    }

    public static class InsufficientBalanceException extends RuntimeException {
        public InsufficientBalanceException(String message) { super(message); }
    }
}
