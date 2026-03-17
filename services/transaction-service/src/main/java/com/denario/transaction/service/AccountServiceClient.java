package com.denario.transaction.service;

import com.denario.transaction.dto.AccountServiceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;

@Service
@Slf4j
public class AccountServiceClient {

    private final WebClient webClient;

    public AccountServiceClient(@Value("${denario.services.account-service-url}") String accountServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(accountServiceUrl)
                .build();
    }

    public AccountServiceDto.BalanceResponse checkBalance(String iban, BigDecimal amount) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/accounts/balance")
                            .queryParam("iban", iban)
                            .queryParam("amount", amount)
                            .build())
                    .retrieve()
                    .bodyToMono(AccountServiceDto.BalanceResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error calling account-service checkBalance: {}", e.getMessage());
            throw new AccountServiceException("Failed to check balance: " + e.getMessage());
        }
    }

    public void updateBalance(String iban, BigDecimal amount) {
        try {
            webClient.patch()
                    .uri("/api/accounts/balance")
                    .bodyValue(new AccountServiceDto.UpdateBalanceRequest(iban, amount))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error calling account-service updateBalance: {}", e.getMessage());
            throw new AccountServiceException("Failed to update balance: " + e.getMessage());
        }
    }

    public static class AccountServiceException extends RuntimeException {
        public AccountServiceException(String message) { super(message); }
    }
}
