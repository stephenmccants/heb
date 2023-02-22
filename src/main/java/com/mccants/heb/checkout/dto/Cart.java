package com.mccants.heb.checkout.dto;

import java.util.List;

/**
 * This is the "Cart" that the Receipt controller expects to receive in JSON format.
 * @param items items in the cart
 */
public record Cart(List<Item> items) { }
