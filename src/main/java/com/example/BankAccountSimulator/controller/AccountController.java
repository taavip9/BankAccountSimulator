package com.example.BankAccountSimulator.controller;

import com.example.BankAccountSimulator.domain.Account;
import com.example.BankAccountSimulator.dto.PaymentDTO;
import com.example.BankAccountSimulator.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.util.List;

@RestController()
@RequestMapping("/accounts")
@CrossOrigin(origins = "http://localhost:4200")
public class AccountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @GetMapping()
    public ResponseEntity<List<Account>> getAccounts() {
        LOGGER.debug("getAccounts() called");
        List<Account> accountList = accountService.getAllAccounts();
        LOGGER.info("Finished fetching accounts, results: {}", accountList);
        return ResponseEntity.ok().body(accountList);
    }

    @PutMapping()
    public ResponseEntity<Account> saveAccount(@RequestBody Account account) {
        LOGGER.debug("saveAccount() called, request body: {}", account);
        return ResponseEntity.ok().body(accountService.updateAccount(account));

    }

    @PostMapping()
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        LOGGER.debug("createAccount() called, request body: {}", account);
        return ResponseEntity.ok().body(accountService.createAccount(account));
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteAccount(@RequestBody Account account) {
        LOGGER.debug("deleteAccount() called, request body: {}", account);
        return ResponseEntity.ok().body(accountService.deleteAccount(account.getId()));
    }


    @PutMapping("/transfer")
    public ResponseEntity<PaymentDTO> transferFundsFromOneAccountToAnother(@RequestBody PaymentDTO paymentDTO) throws ValidationException {
        LOGGER.debug("transferFundsFromOneAccountToAnother() called, request paymentDTO: {}", paymentDTO);
        return ResponseEntity.ok().body(accountService.transferFundsFromOneAccountToAnother(paymentDTO));

    }
}
