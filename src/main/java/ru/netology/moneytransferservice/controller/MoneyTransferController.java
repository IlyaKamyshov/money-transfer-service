package ru.netology.moneytransferservice.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.netology.moneytransferservice.model.ConfirmOperation;
import ru.netology.moneytransferservice.model.Transfer;
import ru.netology.moneytransferservice.response.Response200;
import ru.netology.moneytransferservice.service.TransferService;

@RestController
@CrossOrigin
@Validated
@RequestMapping("/")
public class MoneyTransferController {

    private final TransferService transferService;

    @Autowired
    public MoneyTransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("transfer")
    public Response200 transfer(@RequestBody @Valid Transfer transfer) {
        return transferService.transfer(transfer);
    }

    @PostMapping("confirmOperation")
    public Response200 confirmOperation(@RequestBody @Valid ConfirmOperation confirmOperation) {
        return transferService.confirm(confirmOperation);
    }

}
