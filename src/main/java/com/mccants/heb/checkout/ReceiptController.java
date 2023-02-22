package com.mccants.heb.checkout;

import com.mccants.heb.checkout.dto.Cart;
import com.mccants.heb.checkout.dto.Item;
import com.mccants.heb.checkout.dto.Receipt;
import org.javamoney.moneta.Money;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.money.Monetary;
import java.util.Locale;
import java.util.Optional;

/**
 * Controller for methods providing receipt information.
 */
@RestController
@RequestMapping("/receipt")
public class ReceiptController {

    /**
     * Feature 1 - Takes a cart and provides a receipt with only the grand total.
     * @param cart the Cart whose grand total we will calculate and return in the Receipt
     * @return a Receipt object with the grand total for the cart.
     */
    @PutMapping(path="/total")
    public Receipt total(@RequestBody Cart cart) {
        // Go through the cart and create the grand total
        Optional<Money> grandTotal = cart.items().stream().map(Item::getPrice).reduce(Money::add);
        return new Receipt(grandTotal.orElseGet(() -> Money.of(0, Monetary.getCurrency(Locale.US))));
    }
}
