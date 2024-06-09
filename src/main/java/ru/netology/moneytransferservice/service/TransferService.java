package ru.netology.moneytransferservice.service;

import ru.netology.moneytransferservice.model.ConfirmOperation;
import ru.netology.moneytransferservice.model.Transfer;
import ru.netology.moneytransferservice.response.Response200;

public interface TransferService {
    Response200 transfer(Transfer transfer);
    Response200 confirm(ConfirmOperation confirmation);
}