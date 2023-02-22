package com.mccants.heb.util;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.javamoney.moneta.Money;

import javax.money.Monetary;
import java.util.Locale;

/**
 * Used in Json deserialization to convert dollars and cents into Money objects.
 * <p>
 * NOTE: This assumes US dollars.  For internationalization this will require additional work.
 */
public class NumberToMoneyConverter extends StdConverter<Number, Money> {
    @Override
    public Money convert(Number number) {
        return Money.of(number, Monetary.getCurrency(Locale.US));
    }

}
