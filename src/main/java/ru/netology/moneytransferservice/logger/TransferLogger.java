package ru.netology.moneytransferservice.logger;

public interface TransferLogger {
    void logInfo(String msg);
    void logError(String msg);
}
