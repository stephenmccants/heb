package com.mccants.heb.util;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.javamoney.moneta.Money;

/**
 * Used in Json serialization to a Money object into a number.  The type of currency is discarded.
 */
public class MoneyToNumberConverter extends StdConverter<Money, Number> {
    @Override
    public Number convert(Money money) {
        return money.getNumber();
    }
}
