package com.mccants.heb.checkout.service;

import com.mccants.heb.util.MoneyUtil;
import org.javamoney.moneta.Money;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.money.Monetary;

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
     * Determines the amount of tax on the amount of money passed in
     * @param taxable the amount of money that is subject to sales tax
     * @return the sales tax amount (rounded).
     */
    public Money calculateSalesTax(@Nullable Money taxable) {
        if(taxable == null)
            return MoneyUtil.ZERO;
        return taxable.multiply(TAX_RATE).with(Monetary.getDefaultRounding());
    }
}
