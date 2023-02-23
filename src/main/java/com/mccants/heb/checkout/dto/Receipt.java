package com.mccants.heb.checkout.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mccants.heb.util.MoneyToNumberConverter;
import com.mccants.heb.util.NumberToMoneyConverter;
import org.javamoney.moneta.Money;

/**
 * A Receipt that is generated.
 * @param grandTotal the grand total
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Receipt(
        @JsonDeserialize(converter = NumberToMoneyConverter.class) @JsonSerialize(converter = MoneyToNumberConverter.class) Money subTotal,
        @JsonDeserialize(converter = NumberToMoneyConverter.class) @JsonSerialize(converter = MoneyToNumberConverter.class) Money discountTotal,
        @JsonDeserialize(converter = NumberToMoneyConverter.class) @JsonSerialize(converter = MoneyToNumberConverter.class) Money discountedSubTotal,
        @JsonDeserialize(converter = NumberToMoneyConverter.class) @JsonSerialize(converter = MoneyToNumberConverter.class) Money taxableSubTotal,
        @JsonDeserialize(converter = NumberToMoneyConverter.class) @JsonSerialize(converter = MoneyToNumberConverter.class) Money taxTotal,
        @JsonDeserialize(converter = NumberToMoneyConverter.class) @JsonSerialize(converter = MoneyToNumberConverter.class) Money grandTotal) {
}
