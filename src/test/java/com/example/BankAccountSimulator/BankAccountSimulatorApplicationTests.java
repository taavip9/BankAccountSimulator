package com.example.BankAccountSimulator;

import com.example.BankAccountSimulator.domain.Account;
import com.example.BankAccountSimulator.dto.PaymentDTO;
import com.example.BankAccountSimulator.repository.AccountRepository;
import com.example.BankAccountSimulator.service.AccountService;
import com.example.BankAccountSimulator.util.ConversionUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.bind.ValidationException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@SpringBootTest
class BankAccountSimulatorApplicationTests {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    public void createTestData() {
        if (accountRepository.findAll().isEmpty()) {
            Account eurAccount = new Account();
            eurAccount.setName("eurAccount");
            eurAccount.setCurrency(Account.AccountCurrency.EUR);
            eurAccount.setAmount(new BigDecimal(1000));
            Account usdAccount = new Account();
            usdAccount.setName("usdAccount");
            usdAccount.setCurrency(Account.AccountCurrency.USD);
            usdAccount.setAmount(new BigDecimal(1000));
            accountRepository.save(eurAccount);
            accountRepository.save(usdAccount);
        } else {
            Account eurAccount = accountRepository.findById(1l).get();
            eurAccount.setAmount(new BigDecimal(1000));
            Account usdAccount = accountRepository.findById(2l).get();
            usdAccount.setAmount(new BigDecimal(1000));
            accountRepository.save(eurAccount);
            accountRepository.save(usdAccount);
        }

    }

    @Test
    void testTransferFundsFromEurToEurAccount() {
        Account sourceAccountBeforeTransfer = accountRepository.findById(1l).get();
        Account targetAccountBeforeTransfer = accountRepository.findById(2l).get();
        targetAccountBeforeTransfer.setCurrency(Account.AccountCurrency.EUR);
        targetAccountBeforeTransfer = accountRepository.save(targetAccountBeforeTransfer);
        PaymentDTO paymentDTO = new PaymentDTO(sourceAccountBeforeTransfer, targetAccountBeforeTransfer, new BigDecimal(100));
        boolean isErrorThrown = false;
        try {
            accountService.transferFundsFromOneAccountToAnother(paymentDTO);
        } catch (ValidationException validationException) {
            isErrorThrown = true;
        }
        Account sourceAccountAfterTransfer = accountRepository.findById(1l).get();
        Account targetAccountAfterTransfer = accountRepository.findById(2l).get();
        BigDecimal expectedSourceAccountFunds = sourceAccountBeforeTransfer.getAmount().subtract(paymentDTO.getPaymentAmount()).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal expectedTargetAccountFunds = targetAccountBeforeTransfer.getAmount().add(paymentDTO.getPaymentAmount()).setScale(2, RoundingMode.HALF_EVEN);
        Assertions.assertEquals(expectedSourceAccountFunds, sourceAccountAfterTransfer.getAmount(),
                "Expected source account to have: " + expectedSourceAccountFunds + " EUR, actual amount on source account: " + sourceAccountAfterTransfer.getAmount() + " USD");
        Assertions.assertEquals(expectedTargetAccountFunds, targetAccountAfterTransfer.getAmount(),
                "Expected target account to have: " + expectedTargetAccountFunds + " USD, actual amount on source account: " + targetAccountAfterTransfer.getAmount() + " EUR");
        Assertions.assertEquals(false, isErrorThrown, "An unexpected exception occurred, expected isErrorThrown to be false, but is true");
    }

    @Test
    void testTransferFundsFromEurToUsdAccount() {
        Account sourceAccountBeforeTransfer = accountRepository.findById(1l).get();
        Account targetAccountBeforeTransfer = accountRepository.findById(2l).get();
        PaymentDTO paymentDTO = new PaymentDTO(sourceAccountBeforeTransfer, targetAccountBeforeTransfer, new BigDecimal(100));
        boolean isErrorThrown = false;
        try {
            accountService.transferFundsFromOneAccountToAnother(paymentDTO);
        } catch (ValidationException validationException) {
            isErrorThrown = true;
        }
        Account sourceAccountAfterTransfer = accountRepository.findById(1l).get();
        Account targetAccountAfterTransfer = accountRepository.findById(2l).get();
        BigDecimal expectedSourceAccountFunds = sourceAccountBeforeTransfer.getAmount().subtract(paymentDTO.getPaymentAmount()).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal expectedTargetAccountFunds = targetAccountBeforeTransfer.getAmount().add(ConversionUtil.convertEurToUsd(paymentDTO.getPaymentAmount())).setScale(2, RoundingMode.HALF_EVEN);
        Assertions.assertEquals(expectedSourceAccountFunds, sourceAccountAfterTransfer.getAmount(),
                "Expected source account to have: " + expectedSourceAccountFunds + " EUR, actual amount on source account: " + sourceAccountAfterTransfer.getAmount() + " USD");
        Assertions.assertEquals(expectedTargetAccountFunds, targetAccountAfterTransfer.getAmount(),
                "Expected target account to have: " + expectedTargetAccountFunds + " USD, actual amount on source account: " + targetAccountAfterTransfer.getAmount() + " EUR");
        Assertions.assertEquals(false, isErrorThrown, "An unexpected exception occurred, expected isErrorThrown to be false, but is true");
    }

    @Test
    void testTransferFundsFromUsdToEurAccount() {
        Account sourceAccountBeforeTransfer = accountRepository.findById(2l).get();
        Account targetAccountBeforeTransfer = accountRepository.findById(1l).get();
        PaymentDTO paymentDTO = new PaymentDTO(sourceAccountBeforeTransfer, targetAccountBeforeTransfer, new BigDecimal(100));
        boolean isErrorThrown = false;
        try {
            accountService.transferFundsFromOneAccountToAnother(paymentDTO);
        } catch (ValidationException validationException) {
            isErrorThrown = true;
        }

        Account sourceAccountAfterTransfer = accountRepository.findById(2l).get();
        Account targetAccountAfterTransfer = accountRepository.findById(1l).get();
        BigDecimal expectedSourceAccountFunds = sourceAccountBeforeTransfer.getAmount().subtract(paymentDTO.getPaymentAmount());
        BigDecimal expectedTargetAccountFunds = targetAccountBeforeTransfer.getAmount().add(ConversionUtil.convertUsdToEur(paymentDTO.getPaymentAmount())).setScale(2, RoundingMode.HALF_EVEN);
        Assertions.assertEquals(expectedSourceAccountFunds, sourceAccountAfterTransfer.getAmount(),
                "Expected source account to have: " + expectedSourceAccountFunds + " EUR, actual amount on source account: " + sourceAccountAfterTransfer.getAmount() + " USD");
        Assertions.assertEquals(expectedTargetAccountFunds, targetAccountAfterTransfer.getAmount(),
                "Expected target account to have: " + expectedTargetAccountFunds + " USD, actual amount on source account: " + targetAccountAfterTransfer.getAmount() + " EUR");
        Assertions.assertEquals(false, isErrorThrown, "An unexpected exception occurred, expected isErrorThrown to be false, but is true");

    }

    @Test
    void testTransferZeroFundsBetweenAccounts() {
        String expectedErrorMessage = "Transaction amount can't be negative";
        Account sourceAccountBeforeTransfer = accountRepository.findById(1l).get();
        Account targetAccountBeforeTransfer = accountRepository.findById(2l).get();
        PaymentDTO paymentDTO = new PaymentDTO(sourceAccountBeforeTransfer, targetAccountBeforeTransfer, new BigDecimal(0));
        boolean isErrorThrown = false;
        String errorMessage = null;
        try {
            accountService.transferFundsFromOneAccountToAnother(paymentDTO);
        } catch (ValidationException validationException) {
            isErrorThrown = true;
            errorMessage = validationException.getMessage();
        }
        validateFailedTransferResults(sourceAccountBeforeTransfer, targetAccountBeforeTransfer, accountRepository.findById(1l).get(),
                accountRepository.findById(2l).get(), isErrorThrown, errorMessage, expectedErrorMessage);
    }

    @Test
    void testTransferNegativeFundsBetweenAccounts() {
        String expectedErrorMessage = "Transaction amount can't be negative";
        Account sourceAccountBeforeTransfer = accountRepository.findById(1l).get();
        Account targetAccountBeforeTransfer = accountRepository.findById(2l).get();
        PaymentDTO paymentDTO = new PaymentDTO(sourceAccountBeforeTransfer, targetAccountBeforeTransfer, new BigDecimal(-100));
        boolean isErrorThrown = false;
        String errorMessage = null;
        try {
            accountService.transferFundsFromOneAccountToAnother(paymentDTO);
        } catch (ValidationException validationException) {
            isErrorThrown = true;
            errorMessage = validationException.getMessage();
        }
        validateFailedTransferResults(sourceAccountBeforeTransfer, targetAccountBeforeTransfer, accountRepository.findById(1l).get(),
                accountRepository.findById(2l).get(), isErrorThrown, errorMessage, expectedErrorMessage);
    }

    @Test
    void testTransferMoreFundsThanIsOnTheAccount() {
        String expectedErrorMessage = "Source account eurAccount does not have enough funds to complete the transaction";
        Account sourceAccountBeforeTransfer = accountRepository.findById(1l).get();
        Account targetAccountBeforeTransfer = accountRepository.findById(2l).get();
        PaymentDTO paymentDTO = new PaymentDTO(sourceAccountBeforeTransfer, targetAccountBeforeTransfer, new BigDecimal(8500));
        boolean isErrorThrown = false;
        String errorMessage = null;
        try {
            accountService.transferFundsFromOneAccountToAnother(paymentDTO);
        } catch (ValidationException validationException) {
            isErrorThrown = true;
            errorMessage = validationException.getMessage();
        }
        validateFailedTransferResults(sourceAccountBeforeTransfer, targetAccountBeforeTransfer, accountRepository.findById(1l).get(),
                accountRepository.findById(2l).get(), isErrorThrown, errorMessage, expectedErrorMessage);
    }

    private void validateFailedTransferResults(Account sourceAccountBeforeTransfer, Account targetAccountBeforeTransfer,
                                               Account sourceAccountAfterTransfer, Account targetAccountAfterTransfer, boolean isErrorThrown, String actualErrorMessage, String expectedErrorMessage) {
        BigDecimal expectedSourceAccountFunds = sourceAccountBeforeTransfer.getAmount();
        BigDecimal expectedTargetAccountFunds = targetAccountBeforeTransfer.getAmount();
        Assertions.assertEquals(expectedSourceAccountFunds, sourceAccountAfterTransfer.getAmount(),
                "Expected source account to have: " + expectedSourceAccountFunds + " EUR, actual amount on source account: " + sourceAccountAfterTransfer.getAmount() + " USD");
        Assertions.assertEquals(expectedTargetAccountFunds, targetAccountAfterTransfer.getAmount(),
                "Expected target account to have: " + expectedTargetAccountFunds + " USD, actual amount on source account: " + targetAccountAfterTransfer.getAmount() + " EUR");
        Assertions.assertEquals(true, isErrorThrown, "An unexpected exception occurred, expected isErrorThrown to be true, but is false");
        Assertions.assertEquals(expectedErrorMessage, actualErrorMessage, "An unexpected exception occurred, expected error message to be " + expectedErrorMessage + ", but was " + actualErrorMessage);

    }

}
