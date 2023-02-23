package com.mccants.heb.checkout.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mccants.heb.util.MoneyToNumberConverter;
import com.mccants.heb.util.NumberToMoneyConverter;
import lombok.Data;
import org.javamoney.moneta.Money;

/**
 * This represents a Coupon in a shopping cart.
 * <p>
 * NOTE: This is not a record, because Spring will not handle the deserialization of the price correctly (explodes with an error).
 */
@Data
public class Coupon {
    private final String couponName;
    private final int appliedSku;

    @JsonDeserialize(converter = NumberToMoneyConverter.class)
    @JsonSerialize(converter = MoneyToNumberConverter.class)
    private Money discountPrice;
}
