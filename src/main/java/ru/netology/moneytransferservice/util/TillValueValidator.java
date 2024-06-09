package ru.netology.moneytransferservice.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TillValueValidator implements ConstraintValidator<TillValidator, String> {

    @Override
    public boolean isValid(String valueForValidation, ConstraintValidatorContext context) {

        Pattern pattern =
                Pattern.compile("^([0-9]{2})/([0-9]{2})$");
        Matcher matcher = pattern.matcher(valueForValidation);

        if (!matcher.matches()) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate("Срок действия карты должен быть в формате MM/YY")
                    .addConstraintViolation();
            return false;
        }

        int mm = Integer.parseInt(matcher.group(1));
        if (mm > 12 || mm <= 0) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate("Текущий месяц должен быть в диапазоне [1-12]")
                    .addConstraintViolation();
            return false;
        }

        String cardFromValidTill = valueForValidation.replace("/", "");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyy");
        YearMonth expiration = YearMonth.parse(cardFromValidTill, formatter);
        ZoneId zoneId = ZoneId.of("Europe/Moscow");
        YearMonth currentYearMonth = YearMonth.now(zoneId);
        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate("Срок действия карты истек")
                .addConstraintViolation();
        return !currentYearMonth.isAfter(expiration);

    }

}