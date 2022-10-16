package com.example.BankAccountSimulator.repository;

import com.example.BankAccountSimulator.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
