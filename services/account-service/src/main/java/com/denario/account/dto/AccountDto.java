package com.denario.account.dto;

import com.denario.account.model.Account;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


public class AccountDto {


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateAccountRequest {

        @NotBlank(message = "Owner name is required")
        @Size(min = 2, max = 255, message = "Owner name must be between 2 and 255 characters")
        private String ownerName;

        @NotNull(message = "Account type is required")
        private Account.AccountType accountType;

        @NotBlank(message = "Currency is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter ISO code (e.g. EUR, USD)")
        @Builder.Default
        private String currency = "EUR";


        @DecimalMin(value = "0.00", message = "Initial deposit cannot be negative")
        @Builder.Default
        private BigDecimal initialDeposit = BigDecimal.ZERO;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccountResponse {
        private UUID id;
        private String iban;
        private String ownerName;
        private BigDecimal balance;
        private String currency;
        private Account.AccountType accountType;
        private Account.AccountStatus status;
        private LocalDateTime createdAt;


        public static AccountResponse from(Account account) {
            return AccountResponse.builder()
                    .id(account.getId())
                    .iban(account.getIban())
                    .ownerName(account.getOwnerName())
                    .balance(account.getBalance())
                    .currency(account.getCurrency())
                    .accountType(account.getAccountType())
                    .status(account.getStatus())
                    .createdAt(account.getCreatedAt())
                    .build();
        }
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccountSummary {
        private UUID id;
        private String iban;
        private String ownerName;
        private BigDecimal balance;
        private String currency;
        private Account.AccountStatus status;

        public static AccountSummary from(Account account) {
            return AccountSummary.builder()
                    .id(account.getId())
                    .iban(account.getIban())
                    .ownerName(account.getOwnerName())
                    .balance(account.getBalance())
                    .currency(account.getCurrency())
                    .status(account.getStatus())
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BalanceResponse {
        private UUID accountId;
        private String iban;
        private BigDecimal balance;
        private String currency;
        private boolean sufficient;
    }
}
