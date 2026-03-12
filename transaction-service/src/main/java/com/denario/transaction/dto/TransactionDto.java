package com.denario.transaction.dto;

import com.denario.transaction.model.Transaction;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TransactionDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TransferRequest {

        @NotBlank(message = "Source IBAN is required")
        private String sourceIban;

        @NotBlank(message = "Target IBAN is required")
        private String targetIban;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        @Digits(integer = 15, fraction = 4, message = "Invalid amount format")
        private BigDecimal amount;

        @Size(max = 255, message = "Description cannot exceed 255 characters")
        private String description;

        @NotBlank(message = "Currency is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter ISO code")
        @Builder.Default
        private String currency = "EUR";
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TransactionResponse {
        private UUID id;
        private String sourceIban;
        private String targetIban;
        private BigDecimal amount;
        private String currency;
        private String description;
        private Transaction.TransactionStatus status;
        private Transaction.TransactionType transactionType;
        private String reference;
        private LocalDateTime createdAt;
        private LocalDateTime completedAt;
        private String failureReason;

        public static TransactionResponse from(Transaction t) {
            return TransactionResponse.builder()
                    .id(t.getId())
                    .sourceIban(t.getSourceIban())
                    .targetIban(t.getTargetIban())
                    .amount(t.getAmount())
                    .currency(t.getCurrency())
                    .description(t.getDescription())
                    .status(t.getStatus())
                    .transactionType(t.getTransactionType())
                    .reference(t.getReference())
                    .createdAt(t.getCreatedAt())
                    .completedAt(t.getCompletedAt())
                    .failureReason(t.getFailureReason())
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PagedTransactionsResponse {
        private List<TransactionResponse> transactions;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
    }
}
