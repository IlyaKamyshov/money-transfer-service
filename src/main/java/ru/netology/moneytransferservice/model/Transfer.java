package ru.netology.moneytransferservice.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import ru.netology.moneytransferservice.util.TillValidator;

public final class Transfer {
    @Pattern(regexp = "^[0-9]{16}", message = "Номер карты должен содержать 16 цифр")
    private final String cardFromNumber;

    @TillValidator
    private final String cardFromValidTill;

    @Pattern(regexp = "^[0-9]{3}", message = "CVV карты должен содержать 3 цифры")
    private final String cardFromCVV;

    @Pattern(regexp = "^[0-9]{16}", message = "Номер карты должен содержать 16 цифр")
    private final String cardToNumber;

    @Valid
    private final Amount amount;

    public Transfer(String cardFromNumber,
                    String cardFromValidTill,
                    String cardFromCVV,
                    String cardToNumber,
                    Amount amount) {
        this.cardFromNumber = cardFromNumber;
        this.cardFromValidTill = cardFromValidTill;
        this.cardFromCVV = cardFromCVV;
        this.cardToNumber = cardToNumber;
        this.amount = amount;
    }

    public String getCardFromNumber() {
        return cardFromNumber;
    }

    public String getCardFromValidTill() {
        return cardFromValidTill;
    }

    public String getCardFromCVV() {
        return cardFromCVV;
    }

    public String getCardToNumber() {
        return cardToNumber;
    }

    public Amount getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "cardFromNumber='" + cardFromNumber + '\'' +
                ", cardFromValidTill='" + cardFromValidTill + '\'' +
                ", cardFromCVV='" + cardFromCVV + '\'' +
                ", cardToNumber='" + cardToNumber + '\'' +
                ", amount=" + amount +
                '}';
    }

}