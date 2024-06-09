package ru.netology.moneytransferservice.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class TransferTest {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    Transfer validTransfer = new Transfer(
            "1111111111111111",
            "12/25",
            "111",
            "2222222222222222",
            new Amount(10_000L, "RUR"));

    Set<ConstraintViolation<Transfer>> result;
    ConstraintViolation<Transfer> violation;

    @Test
    void CardFromNumber() {

        Transfer invalidTransfer1 = new Transfer(
                "1111",
                "12/25",
                "111",
                "2222222222222222",
                new Amount(10_000L, "RUR"));

        Transfer invalidTransfer2 = new Transfer(
                "abc1111111111111",
                "12/25",
                "111",
                "2222222222222222",
                new Amount(10_000L, "RUR"));

        result = validator.validate(validTransfer);
        Assertions.assertEquals(0, result.size());

        result = validator.validate(invalidTransfer1);
        Assertions.assertTrue(result.size() > 0);
        violation = result.iterator().next();
        Assertions.assertEquals(violation.getMessage(), "Номер карты должен содержать 16 цифр");

        result = validator.validate(invalidTransfer2);
        Assertions.assertTrue(result.size() > 0);
        violation = result.iterator().next();
        Assertions.assertEquals(violation.getMessage(), "Номер карты должен содержать 16 цифр");

    }

    @Test
    void cardFromValidTill() {

        Transfer invalidTransfer1 = new Transfer(
                "1111111111111111",
                "b2/25",
                "111",
                "2222222222222222",
                new Amount(10_000L, "RUR"));

        Transfer invalidTransfer2 = new Transfer(
                "1111111111111111",
                "12/22",
                "111",
                "2222222222222222",
                new Amount(10_000L, "RUR"));

        result = validator.validate(validTransfer);
        Assertions.assertEquals(0, result.size());

        result = validator.validate(invalidTransfer1);
        Assertions.assertTrue(result.size() > 0);
        violation = result.iterator().next();
        Assertions.assertEquals(violation.getMessage(), "Срок действия карты должен быть в формате MM/YY");

        result = validator.validate(invalidTransfer2);
        Assertions.assertTrue(result.size() > 0);
        violation = result.iterator().next();
        Assertions.assertEquals(violation.getMessage(), "Срок действия карты истек");

    }

    @Test
    void CardFromCVV() {

        Transfer invalidTransfer1 = new Transfer(
                "1111111111111111",
                "12/25",
                "11a",
                "2222222222222222",
                new Amount(10_000L, "RUR"));

        Transfer invalidTransfer2 = new Transfer(
                "1111111111111111",
                "12/25",
                "12345",
                "2222222222222222",
                new Amount(10_000L, "RUR"));

        result = validator.validate(validTransfer);
        Assertions.assertEquals(0, result.size());

        result = validator.validate(invalidTransfer1);
        Assertions.assertTrue(result.size() > 0);
        violation = result.iterator().next();
        Assertions.assertEquals(violation.getMessage(), "CVV карты должен содержать 3 цифры");

        result = validator.validate(invalidTransfer2);
        Assertions.assertTrue(result.size() > 0);
        violation = result.iterator().next();
        Assertions.assertEquals(violation.getMessage(), "CVV карты должен содержать 3 цифры");

    }

    @Test
    void CardToNumber() {

        Transfer invalidTransfer1 = new Transfer(
                "1111111111111111",
                "12/25",
                "111",
                "abc2222222222222",
                new Amount(10_000L, "RUR"));

        Transfer invalidTransfer2 = new Transfer(
                "1111111111111111",
                "12/25",
                "111",
                "2222",
                new Amount(10_000L, "RUR"));

        result = validator.validate(validTransfer);
        Assertions.assertEquals(0, result.size());

        result = validator.validate(invalidTransfer1);
        Assertions.assertTrue(result.size() > 0);
        violation = result.iterator().next();
        Assertions.assertEquals(violation.getMessage(), "Номер карты должен содержать 16 цифр");

        result = validator.validate(invalidTransfer2);
        Assertions.assertTrue(result.size() > 0);
        violation = result.iterator().next();
        Assertions.assertEquals(violation.getMessage(), "Номер карты должен содержать 16 цифр");

    }

    @Test
    void Amount() {

        Transfer invalidTransfer1 = new Transfer(
                "1111111111111111",
                "12/25",
                "111",
                "2222222222222222",
                new Amount(99L, "RUR"));

        Transfer invalidTransfer2 = new Transfer(
                "1111111111111111",
                "12/25",
                "111",
                "2222222222222222",
                new Amount(10_000L, "RUS"));

        result = validator.validate(validTransfer);
        Assertions.assertEquals(0, result.size());

        result = validator.validate(invalidTransfer1);
        Assertions.assertTrue(result.size() > 0);
        violation = result.iterator().next();
        Assertions.assertEquals(violation.getMessage(), "Сумма перевода должна быть не меньше 1 RUR/EUR/USD");

        result = validator.validate(invalidTransfer2);
        Assertions.assertTrue(result.size() > 0);
        violation = result.iterator().next();
        Assertions.assertEquals(violation.getMessage(), "Допустимые для перевода валюты: RUR, EUR и USD");

    }

}