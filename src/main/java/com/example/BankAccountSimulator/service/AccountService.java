package com.example.BankAccountSimulator.service;

import com.example.BankAccountSimulator.domain.Account;
import com.example.BankAccountSimulator.dto.PaymentDTO;
import com.example.BankAccountSimulator.repository.AccountRepository;
import com.example.BankAccountSimulator.util.ConversionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);
    private static final int ZERO = 0;

    @Autowired
    private AccountRepository accountRepository;

    public Account createAccount(Account account) {
        LOGGER.debug("creating account {}", account);
        return accountRepository.save(account);
    }

    public Account updateAccount(Account account) {
        LOGGER.debug("updating account {}", account);
        Optional<Account> accountToBeModifiedOptional = accountRepository.findById(account.getId());
        Account accountToBeModified = null;
        if (accountToBeModifiedOptional.isPresent()) {
            accountToBeModified = accountToBeModifiedOptional.get();
            accountToBeModified.setName(account.getName());
            Account.AccountCurrency newCurrency = account.getCurrency();
            if (!accountToBeModified.getCurrency().equals(newCurrency)) {
                convertAccountCurrency(account, newCurrency);
            }
            accountToBeModified.setAmount(account.getAmount());
            accountToBeModified = accountRepository.save(account);
            LOGGER.debug("account {} updated", account);
        }
        return accountToBeModified;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Void deleteAccount(long accountId) {
        accountRepository.deleteById(accountId);
        LOGGER.debug("account {} deleted", accountId);
        return null;
    }

    public PaymentDTO transferFundsFromOneAccountToAnother(PaymentDTO paymentDTO) throws ValidationException {
        Optional<Account> sourceAccountOptional = accountRepository.findById(paymentDTO.getSourceAccount().getId());
        Optional<Account> targetAccountOptional = accountRepository.findById(paymentDTO.getTargetAccount().getId());
        PaymentDTO paymentDTOAfterTransfer = new PaymentDTO();
        if (sourceAccountOptional.isPresent() && targetAccountOptional.isPresent()) {
            Account sourceAccount = sourceAccountOptional.get();
            Account targetAccount = targetAccountOptional.get();
            LOGGER.debug("Transferring funds from account with id {} to account with id {}", sourceAccount.getId(), targetAccount.getId());
            BigDecimal sourceAccountFunds = sourceAccount.getAmount();
            if (sourceAccountFunds.compareTo(BigDecimal.ZERO) > ZERO && sourceAccountFunds.compareTo(paymentDTO.getPaymentAmount()) >= ZERO) {
                if (paymentDTO.getPaymentAmount().compareTo(BigDecimal.ZERO) <= ZERO) {
                    throw new ValidationException("Transaction amount can't be negative");
                }
                LOGGER.debug("Transfer amount is not null or negative");
                if (sourceAccount.getCurrency().equals(targetAccount.getCurrency())) {
                    LOGGER.debug("Accounts use same currency, transferring amount");
                    targetAccount.setAmount(targetAccount.getAmount().add(paymentDTO.getPaymentAmount()));
                } else {
                    LOGGER.debug("Accounts use different currency, converting funds from {} to {}", sourceAccount.getCurrency(), targetAccount.getCurrency());
                    if (Account.AccountCurrency.EUR.equals(targetAccount.getCurrency())) {
                        targetAccount.setAmount(targetAccount.getAmount().add(ConversionUtil.convertUsdToEur(paymentDTO.getPaymentAmount())));
                    } else if (Account.AccountCurrency.USD.equals(targetAccount.getCurrency())) {
                        targetAccount.setAmount(targetAccount.getAmount().add(ConversionUtil.convertEurToUsd(paymentDTO.getPaymentAmount())));
                    }
                }
                sourceAccount.setAmount(sourceAccount.getAmount().subtract(paymentDTO.getPaymentAmount()));
                paymentDTOAfterTransfer.setSourceAccount(accountRepository.save(sourceAccount));
                paymentDTOAfterTransfer.setTargetAccount(accountRepository.save(targetAccount));
                paymentDTOAfterTransfer.setPaymentAmount(paymentDTO.getPaymentAmount());
                LOGGER.debug("Transfer completed resulting paymentDTOAfterTransfer {}", paymentDTOAfterTransfer);
                return paymentDTOAfterTransfer;
            } else {
                throw new ValidationException("Source account " + sourceAccount.getName() + " does not have enough " +
                        "funds to complete the transaction");
            }
        }
        return null;
    }

    private void convertAccountCurrency(Account account, Account.AccountCurrency newCurrency) {
        LOGGER.debug("Converting account currency from {} to {}", account.getCurrency(), newCurrency);
        if (Account.AccountCurrency.EUR.equals(newCurrency)) {
            account.setAmount(ConversionUtil.convertUsdToEur(account.getAmount()));
            account.setCurrency(Account.AccountCurrency.EUR);
        } else if (Account.AccountCurrency.USD.equals(newCurrency)) {
            account.setAmount(ConversionUtil.convertEurToUsd(account.getAmount()));
            account.setCurrency(Account.AccountCurrency.USD);
        }
        LOGGER.debug("Converting finished");
    }
}
