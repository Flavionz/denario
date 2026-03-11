package com.finflow.account.controller;

import com.finflow.account.dto.AccountDto.*;
import com.finflow.account.service.AccountService;
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

        String userId = jwt.getSubject();
        List<AccountSummary> accounts = accountService.getMyAccounts(userId);
        return ResponseEntity.ok(accounts);
    }


    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        AccountResponse account = accountService.getAccountById(id, userId);
        return ResponseEntity.ok(account);
    }


    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        AccountResponse created = accountService.createAccount(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> checkBalance(
            @RequestParam String iban,
            @RequestParam(defaultValue = "0") BigDecimal amount) {

        BalanceResponse balance = accountService.checkBalance(iban, amount);
        return ResponseEntity.ok(balance);
    }
}
