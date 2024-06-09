package ru.netology.moneytransferservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.netology.moneytransferservice.exception.InputDataException;
import ru.netology.moneytransferservice.exception.TransferOrConfirmException;
import ru.netology.moneytransferservice.logger.TransferLogger;
import ru.netology.moneytransferservice.model.ConfirmOperation;
import ru.netology.moneytransferservice.model.Transfer;
import ru.netology.moneytransferservice.model.TransferStatus;
import ru.netology.moneytransferservice.repository.TransferRepository;
import ru.netology.moneytransferservice.response.Response200;
import ru.netology.moneytransferservice.util.SendSMS;

@Service
public class TransferServiceMap implements TransferService {

    private final TransferRepository transferRepository;
    private final TransferLogger logger;

    @Value("${transfer.commission.percent}")
    private int transferCommissionPercent;

    public TransferServiceMap(TransferRepository transferRepository, TransferLogger logger) {
        this.transferRepository = transferRepository;
        this.logger = logger;
    }

    @Override
    public Response200 transfer(Transfer transfer) {
        transferRepository.addTransfer(transfer);
        return new Response200(transferRepository.getId());
    }

    @Override
    public Response200 confirm(ConfirmOperation confirmOperation) {

        String operationId = confirmOperation.getOperationId();
        String code = confirmOperation.getCode();

        if (!transferRepository.isTransferExist(operationId)) {
            String msg = "Операции " + operationId + " не существует";
            throw new TransferOrConfirmException(msg);
        }

        if (transferRepository.getTransferStatus(operationId) == TransferStatus.DONE) {
            String msg = "Операция " + operationId + " уже завершена";
            throw new InputDataException(msg);
        }

        if (transferRepository.getTransferStatus(operationId) == TransferStatus.ERROR) {
            String msg = "При проведении операции " + operationId + " произошла ошибка";
            throw new InputDataException(msg);
        }

        if (!code.equals(SendSMS.sentSMS())) {
            transferRepository.setTransferStatus(operationId, TransferStatus.ERROR);
            String msg = "Неверный ответный код из SMS при подтверждении операции " + operationId;
            throw new TransferOrConfirmException(msg);
        }

        transferRepository.setTransferStatus(operationId, TransferStatus.DONE);
        long commission = transferRepository.getTransfer(operationId).
                getAmount().getValue() * transferCommissionPercent / 100;
        String msg = "WELL DONE\t" + operationId + ": " + transferRepository.getTransfer(operationId).toString() +
                " commission=" + commission;
        logger.logInfo(msg);
        return new Response200(confirmOperation.getOperationId());

    }

}