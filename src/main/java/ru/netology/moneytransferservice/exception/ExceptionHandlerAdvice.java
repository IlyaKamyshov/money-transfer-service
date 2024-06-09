package ru.netology.moneytransferservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.moneytransferservice.logger.TransferLogger;

import java.util.*;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    private final TransferLogger logger;

    public ExceptionHandlerAdvice(TransferLogger logger) {
        this.logger = logger;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException e) {
        UUID id = UUID.randomUUID();
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.put("message", errorMessage);
            errors.put("id", String.valueOf(id));
        });
        logger.logError(errors.get("id") + " " + errors.get("message"));
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InputDataException.class)
    public Map<String, String> handleInputDataException(InputDataException e) {
        return getErrorMessage(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(TransferOrConfirmException.class)
    public Map<String, String> handleTransferOrConfirmException(TransferOrConfirmException e) {
        return getErrorMessage(e.getMessage());
    }

    private Map<String, String> getErrorMessage(String message) {
        UUID id = UUID.randomUUID();
        Map<String, String> errors = new HashMap<>();
        errors.put("message", message);
        errors.put("id", String.valueOf(id));
        logger.logError(id + " " + message);
        return errors;
    }

}
