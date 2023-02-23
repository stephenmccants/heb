package com.mccants.heb.checkout;

import com.mccants.heb.checkout.dto.Cart;
import com.mccants.heb.checkout.dto.Receipt;
import com.mccants.heb.checkout.service.CartService;
import com.mccants.heb.checkout.service.SalesTaxService;
import com.mccants.heb.util.MoneyUtil;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PutMapping(path="/total")
    public Receipt total(@RequestBody Cart cart) {
        // Go through the cart and create the grand total
        Money grandTotal = cartService.getTotal(cart).orElse(MoneyUtil.ZERO);
        return new Receipt(null, null, grandTotal);
    }

    /**
     * Feature 2 - Takes a cart, calculates the taxes and returns a subTotal, taxTotal and grandTotal in a reciept
     * @param cart the Cart whose totals we will calculate and return in the Receipt
     * @return a Receipt containing subTotal, taxTotal and grandTotal
     */
    @PutMapping(path="/tax")
    public Receipt tax(@RequestBody Cart cart) {
        Money subTotal = cartService.getTotal(cart).orElse(MoneyUtil.ZERO);
        Money taxTotal = salesTaxService.calculateSalesTax(cart.items());
        return new Receipt(subTotal, taxTotal, subTotal.add(taxTotal));
    }
}
