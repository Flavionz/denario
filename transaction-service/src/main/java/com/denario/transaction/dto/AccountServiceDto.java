package com.denario.transaction.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountServiceDto {

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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateBalanceRequest {
        private String iban;
        private BigDecimal amount;
    }
}
