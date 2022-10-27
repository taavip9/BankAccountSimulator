package com.example.BankAccountSimulator.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table
@Data
@NoArgsConstructor
public class Account {

    public enum AccountCurrency {
        USD,
        EUR
    }

    @Id
    @GeneratedValue
    private long id;
    private BigDecimal amount;
    private AccountCurrency currency;
    private String name;

}
