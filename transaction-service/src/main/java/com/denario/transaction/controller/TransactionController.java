package com.denario.transaction.controller;

import com.denario.transaction.dto.TransactionDto.*;
import com.denario.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.transfer(request, jwt.getSubject()));
    }

    @GetMapping("/history")
    public ResponseEntity<PagedTransactionsResponse> getMyTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(
                transactionService.getMyTransactions(jwt.getSubject(), page, size));
    }

    @GetMapping("/history/{iban}")
    public ResponseEntity<PagedTransactionsResponse> getTransactionsByIban(
            @PathVariable String iban,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByIban(iban, page, size));
    }
}
