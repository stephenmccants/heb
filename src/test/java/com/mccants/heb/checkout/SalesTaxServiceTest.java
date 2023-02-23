package com.mccants.heb.checkout;

import com.mccants.heb.checkout.dto.Item;
import com.mccants.heb.checkout.service.SalesTaxService;
import com.mccants.heb.util.MoneyUtil;
import org.assertj.core.util.Lists;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeAll;
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

    private Item noRoundingItem;
    private Item roundDownItem;
    private Item roundUpItem;
    private Item noTaxItem;

    @BeforeAll
    public static void setupUp() {
    }

    @BeforeEach
    public void beforeEach() {
        salesTaxService = new SalesTaxService();
        // Test Data Objects
        noRoundingItem = new Item("noRounding", 1, true, false);
        noRoundingItem.setPrice(MoneyUtil.createUSD(100));
        roundDownItem = new Item("noRounding", 2, true, false);
        roundDownItem.setPrice(MoneyUtil.createUSD(1));
        roundUpItem = new Item("noRounding", 3, true, false);
        roundUpItem.setPrice(MoneyUtil.createUSD(3));
        noTaxItem = new Item("noRounding", 4, false, false);
        noTaxItem.setPrice(MoneyUtil.createUSD(100));
    }

    /**
     * Calculate sales tax with no items
     */
    @Test
    public void testCalculateSalesTax_NoItems() {
        Money tax = salesTaxService.calculateSalesTax(Lists.list());
        assertThat(tax, is(notNullValue()));
        assertThat(tax.getNumberStripped(), is(BigDecimal.valueOf(0)));
    }

    /**
     * Calculate sales tax with one item, no rounding
     */
    @Test
    public void testCalculateSalesTax_OneItem_NoRounding() {
        Money tax = salesTaxService.calculateSalesTax(Lists.list(noRoundingItem));
        assertThat(tax, is(notNullValue()));
        assertThat(tax.getNumberStripped(), is(BigDecimal.valueOf(8.25)));
    }

    /**
     * Calculate sales tax with three items, no rounding
     */
    @Test
    public void testCalculateSalesTax_ThreeItems_NoRounding() {
        Money tax = salesTaxService.calculateSalesTax(Lists.list(noRoundingItem, noRoundingItem, noRoundingItem));
        assertThat(tax, is(notNullValue()));
        assertThat(tax.getNumberStripped(), is(BigDecimal.valueOf(24.75)));
    }

    /**
     * Calculate sales tax with one item, rounding down
     */
    @Test
    public void testCalculateSalesTax_OneItem_RoundDown() {
        Money tax = salesTaxService.calculateSalesTax(Lists.list(roundDownItem));
        assertThat(tax, is(notNullValue()));
        assertThat(tax.getNumberStripped(), is(BigDecimal.valueOf(0.08)));
    }

    /**
     * Calculate sales tax with one item, rounding up
     */
    @Test
    public void testCalculateSalesTax_OneItem_RoundUp() {
        Money tax = salesTaxService.calculateSalesTax(Lists.list(roundUpItem));
        assertThat(tax, is(notNullValue()));
        assertThat(tax.getNumberStripped(), is(BigDecimal.valueOf(0.25)));
    }

    /**
     * Make sure we don't apply taxes to a tax free item
     */
    @Test
    public void testCalculateSalesTax_OneItem_NoTax() {
        Money tax = salesTaxService.calculateSalesTax(Lists.list(noTaxItem));
        assertThat(tax, is(notNullValue()));
        assertThat(tax.getNumberStripped(), is(BigDecimal.valueOf(0)));
    }

    /**
     * Make sure we don't apply taxes to tax free items
     */
    @Test
    public void testCalculateSalesTax_ThreeItems_NoTax() {
        Money tax = salesTaxService.calculateSalesTax(Lists.list(noTaxItem, noTaxItem, noTaxItem));
        assertThat(tax, is(notNullValue()));
        assertThat(tax.getNumberStripped(), is(BigDecimal.valueOf(0)));
    }
}
