package com.mccants.heb.checkout.service;

import com.mccants.heb.checkout.dto.Cart;
import com.mccants.heb.checkout.dto.Coupon;
import com.mccants.heb.checkout.dto.Item;
import com.mccants.heb.util.MoneyUtil;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Provides business logic related to processing Carts.
 */
@Service
public class CartService {

    /**
     * Totals up all the items in a cart
     * @param cart the cart in question
     * @return the total price of all the items in the cart
     */
    public Optional<Money> getTotal(Cart cart) {
        return cart.items().stream().map(Item::getPrice).reduce(Money::add);
    }

    /**
     * Totals up all the items in the cart that are taxable
     * @param cart the cart in question
     * @return the total price of all taxable items
     */
    public Optional<Money> getTaxableTotal(Cart cart) {
        return cart.items().stream().filter(Item::isTaxable).map(Item::getPrice).reduce(Money::add);
    }

    public DiscountedTotals applyCoupons(Cart cart) {
        // Go through all the coupons and apply them to each item in the cart.  Determine the discounted and taxable discounted subtotals
        Money discountTotal = MoneyUtil.ZERO;
        Money taxableDiscount = MoneyUtil.ZERO;
        // Sanity checks
        if (cart == null || cart.coupons() == null || cart.coupons().isEmpty() || cart.items() == null || cart.items().isEmpty())
            return new DiscountedTotals(discountTotal, taxableDiscount);
        // Go through one coupon at a time and apply it
        for(Coupon coupon : cart.coupons()) {
            // Apply this to all the items in the cart
            Optional<Money> couponDiscount = cart.items().stream().filter(item -> item.getSku() == coupon.getAppliedSku())
                    .map(item -> determineCouponDiscount(item, coupon))
                    .filter(Money::isPositive)
                    .reduce(Money::add);
            discountTotal = discountTotal.add(couponDiscount.orElse(MoneyUtil.ZERO));
            Optional<Money> taxableCouponDiscount = cart.items().stream().filter(Item::isTaxable)
                    .filter(item -> item.getSku() == coupon.getAppliedSku())
                    .map(item -> determineCouponDiscount(item, coupon))
                    .filter(Money::isPositive)
                    .reduce(Money::add);
            taxableDiscount = taxableDiscount.add(taxableCouponDiscount.orElse(MoneyUtil.ZERO));
        }
        return new DiscountedTotals(discountTotal, taxableDiscount);
    }

    /**
     * First, make sure the coupon applies to the item, then give the maximum discount
     * - either the price of item or the value of the coupon, which ever is less
     * @param item item to discount
     * @param coupon coupon to apply
     * @return maximum discount
     */
    private Money determineCouponDiscount(Item item, Coupon coupon) {
        if(item.getSku() == coupon.getAppliedSku()) {
            if (item.getPrice().isGreaterThan(coupon.getDiscountPrice()))
                return coupon.getDiscountPrice(); // The discount maximum amount of the coupon
            else
                return item.getPrice(); // Cannot discount the price below zero
        }
        return MoneyUtil.ZERO;
    }

    public record DiscountedTotals(Money discountTotal, Money taxableDiscount) { }
}
