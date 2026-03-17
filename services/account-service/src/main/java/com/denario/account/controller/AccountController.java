package com.denario.account.controller;

import com.denario.account.dto.AccountDto.*;
import com.denario.account.dto.UpdateBalanceRequest;
import com.denario.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/me")
    public ResponseEntity<List<AccountSummary>> getMyAccounts(
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(accountService.getMyAccounts(jwt.getSubject()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(accountService.getAccountById(id, jwt.getSubject()));
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.createAccount(request, jwt.getSubject()));
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> checkBalance(
            @RequestParam String iban,
            @RequestParam(defaultValue = "0") BigDecimal amount) {

        return ResponseEntity.ok(accountService.checkBalance(iban, amount));
    }

    @PatchMapping("/balance")
    public ResponseEntity<Void> updateBalance(@RequestBody UpdateBalanceRequest request) {
        accountService.updateBalance(request.getIban(), request.getAmount());
        return ResponseEntity.noContent().build();
    }
}
