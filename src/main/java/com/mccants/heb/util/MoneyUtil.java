package com.mccants.heb.util;

import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.util.Locale;

/**
 * Convenience methods and static final values to prevent code duplication.
 */
public class MoneyUtil {
    /** Convenience of not looking up the local and currency over and over again. */
    public final static CurrencyUnit USD = Monetary.getCurrency(Locale.US);
    /** Convenience zero US dollars and cents, so we aren't creating it over and over again. */
    public final static Money ZERO =  Money.zero(USD);

    public static Money createUSD(double amount) {
        return Money.of(amount, USD);
    }
}
