package com.example.BankAccountSimulator;

import com.example.BankAccountSimulator.domain.Account;
import com.example.BankAccountSimulator.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;

@SpringBootApplication
public class BankAccountSimulatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankAccountSimulatorApplication.class, args);
	}

}
