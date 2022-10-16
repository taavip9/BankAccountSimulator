package com.example.BankAccountSimulator.dto;

import com.example.BankAccountSimulator.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private Account sourceAccount;
    private Account targetAccount;
    private BigDecimal paymentAmount;
}
