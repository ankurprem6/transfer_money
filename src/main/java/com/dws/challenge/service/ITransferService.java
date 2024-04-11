package com.dws.challenge.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;


public interface ITransferService {

    void transferMoney(String accountFromId, String accountToId, BigDecimal amount);
}
