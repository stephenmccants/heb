package com.mccants.heb.checkout.service;

import com.mccants.heb.util.MoneyUtil;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * There are a number of interesting tax scenarios (it is a complicated domain) that I want to test.
 */
public class SalesTaxServiceTest {
    // Under test
    private SalesTaxService salesTaxService;

    @BeforeEach
    public void beforeEach() {
        salesTaxService = new SalesTaxService();
    }

    /**
     * Calculate sales tax with a null taxable amount
     */
    @Test
    public void testCalculateSalesTax_NullTaxable() {
        Money tax = salesTaxService.calculateSalesTax(null);
        assertThat(tax, is(notNullValue()));
        assertThat(tax.getNumberStripped(), is(BigDecimal.valueOf(0)));
    }

    /**
     * Calculate sales tax with no taxable amount
     */
    @Test
    public void testCalculateSalesTax_ZeroTaxable() {
        Money tax = salesTaxService.calculateSalesTax(MoneyUtil.ZERO);
        assertThat(tax, is(notNullValue()));
        assertThat(tax.getNumberStripped(), is(BigDecimal.valueOf(0)));
    }

    /**
     * Calculate sales tax with no rounding
     */
    @Test
    public void testCalculateSalesTax_NoRounding() {
        Money tax = salesTaxService.calculateSalesTax(MoneyUtil.createUSD(100));
        assertThat(tax, is(notNullValue()));
        assertThat(tax.getNumberStripped(), is(BigDecimal.valueOf(8.25)));
    }

    /**
     * Calculate sales tax rounding down
     */
    @Test
    public void testCalculateSalesTax_RoundDown() {
        Money tax = salesTaxService.calculateSalesTax(MoneyUtil.createUSD(1));
        assertThat(tax, is(notNullValue()));
        assertThat(tax.getNumberStripped(), is(BigDecimal.valueOf(0.08)));
    }

    /**
     * Calculate sales tax rounding up
     */
    @Test
    public void testCalculateSalesTax_RoundUp() {
        Money tax = salesTaxService.calculateSalesTax(MoneyUtil.createUSD(3));
        assertThat(tax, is(notNullValue()));
        assertThat(tax.getNumberStripped(), is(BigDecimal.valueOf(0.25)));
    }
}
