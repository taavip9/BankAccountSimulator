package com.example.BankAccountSimulator.domain;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table
@Data
public class Account {

    public enum AccountCurrency{
        USD,
        EUR
    }

    @Id
    @GeneratedValue
    private long id;
    private BigDecimal amount;
    private AccountCurrency currency;
    private String name;

    public Account() {

    }
    public Account(BigDecimal amount, AccountCurrency currency, String name) {
        this.amount = amount;
        this.currency = currency;
        this.name = name;
    }
}
