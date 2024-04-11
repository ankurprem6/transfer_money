package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.NegativeAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
class TransferServiceUsingLockApiImplTest {

    @Mock
    private AccountsService accountsService;

    @Mock
    private TransferValidator transferValidator;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TransferServiceUsingLockApiImpl transferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void transferMoney_SuccessfulTransfer() {
        // Mock data
        String accountFromId = "fromAccountId";
        String accountToId = "toAccountId";
        BigDecimal transferAmount = BigDecimal.valueOf(100);
        Account accountFrom = new Account(accountFromId, BigDecimal.valueOf(200));
        Account accountTo = new Account(accountToId, BigDecimal.valueOf(100));

        // Mock behavior of accountsService
        when(accountsService.getAccount(accountFromId)).thenReturn(accountFrom);
        when(accountsService.getAccount(accountToId)).thenReturn(accountTo);

        // Mock behavior of transferValidator
        doNothing().when(transferValidator).validateAmount(transferAmount);
        doNothing().when(transferValidator).validateOverDraft(accountFrom.getBalance(), transferAmount);

        // Perform the transfer
        transferService.transferMoney(accountFromId, accountToId, transferAmount);

        assertEquals(BigDecimal.valueOf(100), accountFrom.getBalance());
        assertEquals(BigDecimal.valueOf(200), accountTo.getBalance());

        // Verify that notification was sent
        verify(notificationService).notifyAboutTransfer(accountFrom, "amount transferred");
    }

    @Test
    public void testTransferMoney_NegativeAmount() {
        // Mocking account data
        String accountFromId = "fromAccountId";
        String accountToId = "toAccountId";
        BigDecimal amount = BigDecimal.valueOf(-100); // negative amount
        Account accountFrom = new Account(accountFromId, BigDecimal.valueOf(200));
        Account accountTo = new Account(accountToId, BigDecimal.valueOf(100));

        when(accountsService.getAccount(accountFromId)).thenReturn(accountFrom);
        when(accountsService.getAccount(accountToId)).thenReturn(accountTo);

        // Mock behavior of transferValidator
        doThrow(NegativeAmountException.class).when(transferValidator).validateAmount(amount);

        // Calling the method under test
        assertThrows(NegativeAmountException.class, () -> transferService.transferMoney(accountFromId, accountToId, amount));
    }
}
