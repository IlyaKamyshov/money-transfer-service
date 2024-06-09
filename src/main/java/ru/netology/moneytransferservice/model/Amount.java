package ru.netology.moneytransferservice.model;

import jakarta.validation.constraints.Min;
import ru.netology.moneytransferservice.util.CurrencyValidator;

public final class Amount {
    @Min(value = 100, message = "Сумма перевода должна быть не меньше 1 RUR/EUR/USD")
    private final long value;
    @CurrencyValidator(enumClass = Currency.class, ignoreCase = true,
            message = "Допустимые для перевода валюты: RUR, EUR и USD")
    private final String currency;

    public Amount(long value, String currency) {
        this.value = value;
        this.currency = currency;
    }

    public long getValue() {
        return value;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "Amount{" +
                "value=" + value +
                ", currency='" + currency + '\'' +
                '}';
    }

}
