package com.mccants.heb.checkout.service;

import com.mccants.heb.checkout.dto.Cart;
import com.mccants.heb.checkout.dto.Item;
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
     * @return the total of all the items in the cart
     */
    public Optional<Money> getTotal(Cart cart) {
        return cart.items().stream().map(Item::getPrice).reduce(Money::add);
    }
}
