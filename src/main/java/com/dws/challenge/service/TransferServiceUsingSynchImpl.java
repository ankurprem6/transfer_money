package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

//This implementation using synchronization however this might cause the deadlock when invoke simultaneously

@Service("transferServiceUsingSynchronisation")
@Slf4j
public class TransferServiceUsingSynchImpl implements ITransferService {

    @Autowired
    AccountsService accountsService;

    @Autowired
    TransferValidator transferValidator;

    @Autowired
    NotificationService service;

    /**
     * This function will transfer money
     * @param accountFromId : Account Number from which we have to deduct the amount
     * @param accountToId : Account Number on which we have to credit the amount
     * @param amount : A positive amount to be transferred
     */
    public void transferMoney(String accountFromId, String accountToId, BigDecimal amount) {
        //schriozed

        transferValidator.validateAmount(amount);

        Account accountFrom = accountsService.getAccount(accountFromId);
        Account accountTo = accountsService.getAccount(accountToId);
        synchronized (accountFrom) {
            synchronized (accountTo) {

                BigDecimal finalBalanceOfFrom = accountFrom.getBalance().subtract(amount);
                transferValidator.validateOverDraft(finalBalanceOfFrom, finalBalanceOfFrom);

                BigDecimal finalBalanceOfTo = accountTo.getBalance().add(amount);

                accountFrom.setBalance(finalBalanceOfFrom);
                accountTo.setBalance(finalBalanceOfTo);

                service.notifyAboutTransfer(accountFrom, "amount transferred");

            }
        }
    }
}
