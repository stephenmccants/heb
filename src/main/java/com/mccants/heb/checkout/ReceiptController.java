package com.mccants.heb.checkout;

import com.mccants.heb.checkout.dto.Cart;
import com.mccants.heb.checkout.dto.Receipt;
import com.mccants.heb.checkout.service.CartService;
import com.mccants.heb.checkout.service.SalesTaxService;
import com.mccants.heb.util.MoneyUtil;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for methods providing receipt information.
 */
@RestController
@RequestMapping("/receipt")
public class ReceiptController {

    @Autowired
    private CartService cartService;
    @Autowired
    private SalesTaxService salesTaxService;

    /**
     * Feature 1 - Takes a cart and provides a receipt with only the grand total.
     * @param cart the Cart whose grand total we will calculate and return in the Receipt
     * @return a Receipt object with the grand total for the cart.
     */
    @PostMapping(path="/total")
    public Receipt total(@RequestBody Cart cart) {
        // Go through the cart and create the grand total
        Money grandTotal = cartService.getTotal(cart).orElse(MoneyUtil.ZERO);
        return new Receipt(null, null, null, null,null, grandTotal);
    }

    /**
     * Feature 2 - Takes a cart, calculates the taxes and returns a subTotal, taxTotal and grandTotal in a receipt
     * @param cart the Cart whose totals we will calculate and return in the Receipt
     * @return a Receipt containing subTotal, taxTotal and grandTotal
     */
    @PostMapping(path="/tax")
    public Receipt tax(@RequestBody Cart cart) {
        Money subTotal = cartService.getTotal(cart).orElse(MoneyUtil.ZERO);
        Money taxableSubTotal = cartService.getTaxableTotal(cart).orElse(MoneyUtil.ZERO);
        Money taxTotal = salesTaxService.calculateSalesTax(taxableSubTotal);
        return new Receipt(subTotal, null, null, null, taxTotal, subTotal.add(taxTotal));
    }

    /**
     * Feature 3 - Takes a cart, calculates the taxes and returns a subTotal, taxableSubTotal, taxTotal and grandTotal in a receipt
     * @param cart the Cart whose totals we will calculate and return in the Receipt
     * @return a Receipt containing subTotal, taxableSubTotal, taxTotal and grandTotal
     */
    @PostMapping(path="/taxWithSubtotal")
    public Receipt taxWithSubTotal(@RequestBody Cart cart) {
        Money subTotal = cartService.getTotal(cart).orElse(MoneyUtil.ZERO);
        Money taxableSubTotal = cartService.getTaxableTotal(cart).orElse(MoneyUtil.ZERO);
        Money taxTotal = salesTaxService.calculateSalesTax(taxableSubTotal);
        return new Receipt(subTotal, null, null, taxableSubTotal, taxTotal, subTotal.add(taxTotal));
    }

    /**
     * Feature 4 - Takes a cart with optional coupons, calculates the taxes and returns a subTotal, discountTotal, discountedSubTotal,
     * taxableSubTotal, taxTotal and grandTotal in a receipt
     * @param cart the cart to handle.  May or may not have coupons
     * @return a receipt containing thes ubTotal, discountTotal, discountedSubTotal, taxableSubTotal, taxTotal and grandTotal.
     */
    @PostMapping(path="/totalWithCoupons")
    public Receipt coupons(@RequestBody Cart cart) {
        Money subTotal = cartService.getTotal(cart).orElse(MoneyUtil.ZERO);
        CartService.DiscountedTotals discountedTotals = cartService.applyCoupons(cart);
        Money discountTotal = discountedTotals.discountTotal();
        Money discountedSubTotal = subTotal.subtract(discountTotal);
        Money taxableSubTotal = cartService.getTaxableTotal(cart).orElse(MoneyUtil.ZERO).subtract(discountedTotals.taxableDiscount());
        Money taxTotal = salesTaxService.calculateSalesTax(taxableSubTotal);
        return new Receipt(subTotal, discountTotal, discountedSubTotal, taxableSubTotal, taxTotal, discountedSubTotal.add(taxTotal));
    }
}
