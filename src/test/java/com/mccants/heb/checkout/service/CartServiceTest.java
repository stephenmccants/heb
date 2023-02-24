package com.mccants.heb.checkout.service;

import com.mccants.heb.checkout.dto.Cart;
import com.mccants.heb.checkout.dto.Coupon;
import com.mccants.heb.checkout.dto.Item;
import com.mccants.heb.util.MoneyUtil;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CartServiceTest {
    // Under test
    private CartService cartService;

    // Useful data objects
    private Item item1;
    private Item item1_duplicate;
    private Item item2;
    private Item item3;
    // Coupons
    private Coupon coupon1;
    private Coupon coupon2;
    private Coupon coupon3;

    @BeforeEach
    public void beforeEach() {
        cartService = new CartService();
        // Items
        item1 = new Item("One", 1, true, false);
        item1.setPrice(MoneyUtil.createUSD(10.50));
        item1_duplicate = new Item("One Duplicate", 1, true, false);
        item1_duplicate.setPrice(MoneyUtil.createUSD(10.50));
        item2 = new Item("Two", 2, true, false);
        item2.setPrice(MoneyUtil.createUSD(1.19));
        item3 = new Item("Three", 3, false, false);
        item3.setPrice(MoneyUtil.createUSD(9.99));
        // Coupons
        coupon1 = new Coupon("couponOne", 1);
        coupon1.setDiscountPrice(MoneyUtil.createUSD(5.01));
        coupon2 = new Coupon("couponTwo", 2);
        coupon2.setDiscountPrice(MoneyUtil.createUSD(2.00));
        coupon3 = new Coupon("coupon3", 3);
        coupon3.setDiscountPrice(MoneyUtil.createUSD(2.50));
    }

    @Test
    public void testApplyCoupons_NullCart() {
        CartService.DiscountedTotals result = cartService.applyCoupons(null);
        assertThat(result.discountTotal().getNumberStripped(), is(BigDecimal.valueOf(0)));
        assertThat(result.taxableDiscount().getNumberStripped(), is(BigDecimal.valueOf(0)));
    }

    @Test
    public void testApplyCoupons_EmptyCart() {
        CartService.DiscountedTotals result = cartService.applyCoupons(new Cart(null, null));
        assertThat(result.discountTotal().getNumberStripped(), is(BigDecimal.valueOf(0)));
        assertThat(result.taxableDiscount().getNumberStripped(), is(BigDecimal.valueOf(0)));
    }

    /**
     * One coupon that applies to one item in the cart.  The item is taxable
     */
    @Test
    public void testApplyCoupons_OneCoupon_Taxable() {
        CartService.DiscountedTotals result = cartService.applyCoupons(new Cart(Lists.list(item1), Lists.list(coupon1)));
        assertThat(result.discountTotal().getNumberStripped(), is(BigDecimal.valueOf(5.01)));
        assertThat(result.taxableDiscount().getNumberStripped(), is(BigDecimal.valueOf(5.01)));
    }

    /**
     * One coupon that applies to one item in the cart.  The item is not taxable
     */
    @Test
    public void testApplyCoupons_OneCoupon_NotTaxable() {
        CartService.DiscountedTotals result = cartService.applyCoupons(new Cart(Lists.list(item3), Lists.list(coupon3)));
        assertThat(result.discountTotal().getNumberStripped(), is(BigDecimal.valueOf(2.50)));
        assertThat(result.taxableDiscount().getNumberStripped(), is(BigDecimal.valueOf(0)));
    }

    /**
     * One coupon that applies to one item in the cart.  THe coupon is worth more than the item
     */
    @Test
    public void testApplyCoupons_OneCoupon_ExceedsPrice() {
        CartService.DiscountedTotals result = cartService.applyCoupons(new Cart(Lists.list(item2), Lists.list(coupon2)));
        assertThat(result.discountTotal().getNumberStripped(), is(BigDecimal.valueOf(1.19)));
        assertThat(result.taxableDiscount().getNumberStripped(), is(BigDecimal.valueOf(1.19)));
    }

    /**
     * One coupon that applies to one item in the cart.  The coupon does not apply
     */
    @Test
    public void testApplyCoupons_OneCoupon_DoesNotApply() {
        CartService.DiscountedTotals result = cartService.applyCoupons(new Cart(Lists.list(item2), Lists.list(coupon3)));
        assertThat(result.discountTotal().getNumberStripped(), is(BigDecimal.valueOf(0)));
        assertThat(result.taxableDiscount().getNumberStripped(), is(BigDecimal.valueOf(0)));
    }

    /**
     * Three items, the coupon applies to two of them, but not the other
     */
    @Test
    public void testApplyCoupons_ThreeItems_OneCoupon() {
        CartService.DiscountedTotals result = cartService.applyCoupons(new Cart(Lists.list(item1, item1_duplicate, item2), Lists.list(coupon1)));
        assertThat(result.discountTotal().getNumberStripped(), is(BigDecimal.valueOf(10.02)));
        assertThat(result.taxableDiscount().getNumberStripped(), is(BigDecimal.valueOf(10.02)));
    }

    /**
     * Three items, the coupons applies to them
     */
    @Test
    public void testApplyCoupons_ThreeItems_TwoCoupon() {
        CartService.DiscountedTotals result = cartService.applyCoupons(new Cart(Lists.list(item1, item1_duplicate, item2), Lists.list(coupon1, coupon2)));
        assertThat(result.discountTotal().getNumberStripped(), is(BigDecimal.valueOf(11.21)));
        assertThat(result.taxableDiscount().getNumberStripped(), is(BigDecimal.valueOf(11.21)));
    }

    /**
     * Four items, the coupons applies to them
     */
    @Test
    public void testApplyCoupons_FourItems_ThreeCoupon() {
        CartService.DiscountedTotals result = cartService.applyCoupons(new Cart(Lists.list(item1, item1_duplicate, item2, item3), Lists.list(coupon1, coupon2, coupon3)));
        assertThat(result.discountTotal().getNumberStripped(), is(BigDecimal.valueOf(13.71)));
        assertThat(result.taxableDiscount().getNumberStripped(), is(BigDecimal.valueOf(11.21)));
    }
}
