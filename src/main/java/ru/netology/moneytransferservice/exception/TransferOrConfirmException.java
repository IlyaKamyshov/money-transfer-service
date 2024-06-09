package ru.netology.moneytransferservice.exception;

public class TransferOrConfirmException extends RuntimeException {

    public TransferOrConfirmException(String msg) {
        super(msg);
    }

}
