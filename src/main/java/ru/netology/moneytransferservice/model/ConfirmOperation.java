package ru.netology.moneytransferservice.model;


import jakarta.validation.constraints.Pattern;

public final class ConfirmOperation {
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "Неверный формат operationId")
    private final String operationId;
    @Pattern(regexp = "^[0-9]{4}", message = "Код подтверждения должен содержать 4 цифры")
    private final String code;

    public ConfirmOperation(String operationId, String code) {
        this.operationId = operationId;
        this.code = code;
    }

    public String getOperationId() {
        return operationId;
    }

    public String getCode() {
        return code;
    }

}
