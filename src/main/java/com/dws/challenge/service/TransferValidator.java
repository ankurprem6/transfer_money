package com.dws.challenge.service;

import com.dws.challenge.exception.NegativeAmountException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class TransferValidator {

    public void validateOverDraft(BigDecimal finalBalanceOfFrom, BigDecimal amount) {
        if (finalBalanceOfFrom.compareTo(amount) == -1) {
            log.info("We do not support overdrafts!");
            throw new NegativeAmountException(
                    "We do not support overdrafts!");
        }
    }

    public void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) == -1) {
            log.info("Amount must be positive");
            throw new NegativeAmountException(
                    "Amount " + amount + " must be positive");
        }
    }
}
