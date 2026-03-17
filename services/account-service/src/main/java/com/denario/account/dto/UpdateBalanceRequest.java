package com.denario.account.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBalanceRequest {
    private String iban;
    private BigDecimal amount;
}
