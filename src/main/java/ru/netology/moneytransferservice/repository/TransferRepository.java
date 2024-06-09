package ru.netology.moneytransferservice.repository;

import ru.netology.moneytransferservice.model.Transfer;
import ru.netology.moneytransferservice.model.TransferStatus;

public interface TransferRepository {
    void addTransfer(Transfer transfer);
    String getId();
    boolean isTransferExist(String id);
    void setTransferStatus(String id, TransferStatus transferStatus);
    TransferStatus getTransferStatus(String id);
    Transfer getTransfer(String id);
}