package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

//This implementation is using Lock API where all or none lock strategy will be used to avoid deadlock

@Service("transferServiceUsingLockApi")
@Slf4j
public class TransferServiceUsingLockApiImpl implements ITransferService {

    @Autowired
    AccountsService accountsService;

    @Autowired
    TransferValidator transferValidator;

    @Autowired
    NotificationService service;

    Map<String, ReentrantLock> accountLockMap = new ConcurrentHashMap<>();

    /**
     * This function will transfer money
     * @param accountFromId : Account Number from which we have to deduct the amount
     * @param accountToId : Account Number on which we have to credit the amount
     * @param amount : A positive amount to be transferred
     */
    @Override
    public void transferMoney(String accountFromId, String accountToId, BigDecimal amount) {

        transferValidator.validateAmount(amount);

        Account accountFrom = accountsService.getAccount(accountFromId);
        Account accountTo = accountsService.getAccount(accountToId);

        if (acquireLock(accountFrom, accountTo)) {

            BigDecimal finalBalanceOfFrom = accountFrom.getBalance().subtract(amount);
            transferValidator.validateOverDraft(finalBalanceOfFrom, amount);

            BigDecimal finalBalanceOfTo = accountTo.getBalance().add(amount);

            accountFrom.setBalance(finalBalanceOfFrom);
            accountTo.setBalance(finalBalanceOfTo);

            releaseLock(accountFrom, accountTo);

            service.notifyAboutTransfer(accountFrom, "amount transferred");

        }

    }


    private void releaseLock(Account accountFrom, Account accountTo) {
        accountLockMap.get(accountFrom.getAccountId()).unlock();
        accountLockMap.get(accountTo.getAccountId()).unlock();
    }


    private boolean acquireLock(Account accountFrom, Account accountTo) {

        accountLockMap.putIfAbsent(accountFrom.getAccountId(), new ReentrantLock());
        accountLockMap.putIfAbsent(accountTo.getAccountId(), new ReentrantLock());

        ReentrantLock accountFromLock = accountLockMap.get(accountFrom.getAccountId());
        ReentrantLock accountToLock = accountLockMap.get(accountTo.getAccountId());

        Boolean isBothAccLock = false;

        while (!isBothAccLock) {
            if (accountFromLock.tryLock()) {
                if (!accountToLock.isLocked()) {
                    accountToLock.tryLock();
                    isBothAccLock = true;
                } else {
                    accountFromLock.unlock();
                }
            }
        }

        return isBothAccLock;
    }
}
