package com.example.BankAccountSimulator.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class ConversionUtil {
    public static final BigDecimal EUR_TO_USD_CONVERSION_RATE = new BigDecimal(0.80);

    public static BigDecimal convertUsdToEur(BigDecimal amountInUsd) {
        return amountInUsd.divide(EUR_TO_USD_CONVERSION_RATE,  new MathContext(2, RoundingMode.HALF_EVEN));
    }

    public static BigDecimal convertEurToUsd(BigDecimal amountInEur) {
        return amountInEur.multiply(EUR_TO_USD_CONVERSION_RATE).setScale(2, RoundingMode.HALF_EVEN);
    }

}
