package ru.netology.moneytransferservice.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.netology.moneytransferservice.exception.InputDataException;
import ru.netology.moneytransferservice.exception.TransferOrConfirmException;
import ru.netology.moneytransferservice.logger.TransferLogger;
import ru.netology.moneytransferservice.model.Amount;
import ru.netology.moneytransferservice.model.ConfirmOperation;
import ru.netology.moneytransferservice.model.Transfer;
import ru.netology.moneytransferservice.model.TransferStatus;
import ru.netology.moneytransferservice.repository.TransferRepository;
import ru.netology.moneytransferservice.repository.TransferRepositoryMap;

class TransferServiceTest {

    Transfer validTransfer = new Transfer(
            "1111111111111111",
            "12/24",
            "111",
            "2222222222222222",
            new Amount(10_000L, "RUR"));

    TransferLogger logger = Mockito.mock();

    @Test
    void transfer() {

        TransferRepository transferRepository = new TransferRepositoryMap(logger);
        TransferService transferService = new TransferServiceMap(transferRepository, logger);
        String operationId = transferService.transfer(validTransfer).operationId();
        String status = transferRepository.getTransferStatus(operationId).toString();

        Assertions.assertEquals("IN_PROGRESS", status);

    }

    @Test
    void confirmValid() {

        String operationId = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

        TransferRepository transferRepository = Mockito.mock(TransferRepository.class);
        Mockito.when(transferRepository.getId()).thenReturn(operationId);
        Mockito.when(transferRepository.isTransferExist(operationId)).thenReturn(true);
        Mockito.when(transferRepository.getTransfer(operationId)).thenReturn(validTransfer);

        ConfirmOperation confirmOperation = Mockito.mock(ConfirmOperation.class);
        Mockito.when(confirmOperation.getOperationId()).thenReturn(operationId);
        Mockito.when(confirmOperation.getCode()).thenReturn("0000");

        TransferService transferService = new TransferServiceMap(transferRepository, logger);
        String response200 = transferService.confirm(confirmOperation).operationId();

        Assertions.assertEquals(operationId, response200);

    }

    @Test
    void confirmTransactionExist() {

        String operationId = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

        String invalidOperationId = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb";

        TransferRepository transferRepository = Mockito.mock(TransferRepository.class);
        Mockito.when(transferRepository.getId()).thenReturn(operationId);
        Mockito.when(transferRepository.isTransferExist(operationId)).thenReturn(true);
        Mockito.when(transferRepository.getTransfer(operationId)).thenReturn(validTransfer);

        ConfirmOperation confirmOperation = Mockito.mock(ConfirmOperation.class);
        Mockito.when(confirmOperation.getOperationId()).thenReturn(invalidOperationId);
        Mockito.when(confirmOperation.getCode()).thenReturn("0000");

        TransferService transferService = new TransferServiceMap(transferRepository, logger);
        Exception exception = Assertions.assertThrows(TransferOrConfirmException.class,
                () -> transferService.confirm(confirmOperation));

        Assertions.assertEquals(exception.getMessage(), "Операции " + invalidOperationId + " не существует");

    }

    @Test
    void confirmTransferStatusDone() {

        String operationId = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

        TransferRepository transferRepository = Mockito.mock(TransferRepository.class);
        Mockito.when(transferRepository.getId()).thenReturn(operationId);
        Mockito.when(transferRepository.isTransferExist(operationId)).thenReturn(true);
        Mockito.when(transferRepository.getTransferStatus(operationId)).thenReturn(TransferStatus.DONE);
        Mockito.when(transferRepository.getTransfer(operationId)).thenReturn(validTransfer);

        ConfirmOperation confirmOperation = Mockito.mock(ConfirmOperation.class);
        Mockito.when(confirmOperation.getOperationId()).thenReturn(operationId);
        Mockito.when(confirmOperation.getCode()).thenReturn("0000");

        TransferService transferService = new TransferServiceMap(transferRepository, logger);
        Exception exception = Assertions.assertThrows(InputDataException.class,
                () -> transferService.confirm(confirmOperation));

        Assertions.assertEquals(exception.getMessage(), "Операция " + operationId + " уже завершена");

    }

    @Test
    void confirmTransferStatusError() {

        String operationId = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

        TransferRepository transferRepository = Mockito.mock(TransferRepository.class);
        Mockito.when(transferRepository.getId()).thenReturn(operationId);
        Mockito.when(transferRepository.isTransferExist(operationId)).thenReturn(true);
        Mockito.when(transferRepository.getTransferStatus(operationId)).thenReturn(TransferStatus.ERROR);
        Mockito.when(transferRepository.getTransfer(operationId)).thenReturn(validTransfer);

        ConfirmOperation confirmOperation = Mockito.mock(ConfirmOperation.class);
        Mockito.when(confirmOperation.getOperationId()).thenReturn(operationId);
        Mockito.when(confirmOperation.getCode()).thenReturn("0000");

        TransferService transferService = new TransferServiceMap(transferRepository, logger);
        Exception exception = Assertions.assertThrows(InputDataException.class,
                () -> transferService.confirm(confirmOperation));

        Assertions.assertEquals(exception.getMessage(),
                "При проведении операции " + operationId + " произошла ошибка");

    }

    @Test
    void confirmTransferStatusErrorSMSCode() {

        String operationId = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

        TransferRepository transferRepository = Mockito.mock(TransferRepository.class);
        Mockito.when(transferRepository.getId()).thenReturn(operationId);
        Mockito.when(transferRepository.isTransferExist(operationId)).thenReturn(true);
        Mockito.when(transferRepository.getTransferStatus(operationId)).thenReturn(TransferStatus.IN_PROGRESS);
        Mockito.when(transferRepository.getTransfer(operationId)).thenReturn(validTransfer);

        ConfirmOperation confirmOperation = Mockito.mock(ConfirmOperation.class);
        Mockito.when(confirmOperation.getOperationId()).thenReturn(operationId);
        Mockito.when(confirmOperation.getCode()).thenReturn("0001");

        TransferService transferService = new TransferServiceMap(transferRepository, logger);
        Exception exception = Assertions.assertThrows(TransferOrConfirmException.class,
                () -> transferService.confirm(confirmOperation));

        Assertions.assertEquals(exception.getMessage(),
                "Неверный ответный код из SMS при подтверждении операции " + operationId);

    }

}