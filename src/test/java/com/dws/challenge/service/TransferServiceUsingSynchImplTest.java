package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
public class TransferServiceUsingSynchImplTest {

    @Mock
    private AccountsService accountsService;

    @Mock
    private TransferValidator transferValidator;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TransferServiceUsingSynchImpl transferService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void transferMoney_ValidTransfer_SuccessfulTransfer() {
        // Arrange
        String accountFromId = "123";
        String accountToId = "456";
        BigDecimal amount = BigDecimal.valueOf(100);

        Account accountFrom = new Account(accountFromId, BigDecimal.valueOf(500));
        Account accountTo = new Account(accountToId, BigDecimal.valueOf(200));

        Mockito.when(accountsService.getAccount(accountFromId)).thenReturn(accountFrom);
        Mockito.when(accountsService.getAccount(accountToId)).thenReturn(accountTo);
        Mockito.doNothing().when(transferValidator).validateAmount(amount);
        Mockito.doNothing().when(transferValidator).validateOverDraft(Mockito.any(), Mockito.any());
        Mockito.doNothing().when(notificationService).notifyAboutTransfer(Mockito.any(), Mockito.any());

        // Act
        transferService.transferMoney(accountFromId, accountToId, amount);

        // Assert
        Assertions.assertEquals(BigDecimal.valueOf(400), accountFrom.getBalance());
        Assertions.assertEquals(BigDecimal.valueOf(300), accountTo.getBalance());
    }

}
