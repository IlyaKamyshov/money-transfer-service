package ru.netology.moneytransferservice.repository;

import org.springframework.stereotype.Repository;
import ru.netology.moneytransferservice.logger.TransferLogger;
import ru.netology.moneytransferservice.model.Transfer;
import ru.netology.moneytransferservice.model.TransferStatus;


import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TransferRepositoryMap implements TransferRepository {

    private final Map<String, Transfer> transfers = new ConcurrentHashMap<>();
    private final Map<String, TransferStatus> transferStatus = new ConcurrentHashMap<>();
    private String id;

    private final TransferLogger logger;

    public TransferRepositoryMap(TransferLogger logger) {
        this.logger = logger;
    }

    @Override
    public void addTransfer(Transfer transfer) {
        id = UUID.randomUUID().toString();
        transfers.put(id, transfer);
        transferStatus.put(id, TransferStatus.IN_PROGRESS);
        String msg = "IN PROGRESS\t" + id + ": " + getTransfer(id).toString();
        logger.logInfo(msg);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isTransferExist(String id) {
        return transfers.containsKey(id);
    }

    @Override
    public void setTransferStatus(String id, TransferStatus transferStatus) {
        this.transferStatus.put(id, transferStatus);
    }

    @Override
    public TransferStatus getTransferStatus(String id) {
        return transferStatus.get(id);
    }

    @Override
    public Transfer getTransfer(String id) {
        return transfers.get(id);
    }

}
