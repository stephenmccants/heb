package com.mccants.heb.checkout.service;

import com.mccants.heb.checkout.dto.Item;
import com.mccants.heb.util.MoneyUtil;
import org.javamoney.moneta.Money;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.money.Monetary;
import java.util.List;
import java.util.Optional;

/**
 * Provides business logic related to calculating sales taxes.
 * <p>
 * NOTE: This contains a great simplification by assuming the sales tax is always 8.25% (max allowed in Texas).  In reality,
 * sales tax is a complicated calculation as you have multiple overlapping taxing authorities and taxes have to be
 * sent to the correct authorities for the correct amount.
 */
@Service
public class SalesTaxService {
    /** Grow over simplification that the sales tax rate is the same everywhere */
    public final double TAX_RATE = 0.0825;

    /**
     * Goes through a cart and figures out what the sales tax should be
     * @param items a list of items that may or may not be taxable.
     * @return the tax amount rounded to the nearest cent
     */
    public @NonNull Money calculateSalesTax(@NonNull List<Item> items) {
        // First filter out the non-taxable items, then get the price and multiply by the tax rate and return the sum of the results
        Optional<Money> taxAmount =  items.stream().filter(Item::isTaxable)
                .map(Item::getPrice).map((price) -> price.multiply(TAX_RATE))
                .reduce(Money::add);
        return taxAmount.orElse(MoneyUtil.ZERO).with(Monetary.getDefaultRounding()); // Round off the fractional cents
    }
}
