package com.dws.challenge.web;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.NegativeAmountException;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.ITransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.math.BigDecimal;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

  private final AccountsService accountsService;

  private final ITransferService iTransferService;

  @Autowired
  public AccountsController(AccountsService accountsService, @Qualifier("transferServiceUsingLockApi") ITransferService iTransferService) {
    this.accountsService = accountsService;
    this.iTransferService = iTransferService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
    log.info("Creating account {}", account);

    try {
    this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public Account getAccount(@PathVariable String accountId) {
    log.info("Retrieving account for id {}", accountId);
    return this.accountsService.getAccount(accountId);
  }


  @PostMapping(path = "/{transfer-money}")
  public String transferMoney(@RequestParam String accountFromId,
                                              @RequestParam String accountToId,
                                              @RequestParam BigDecimal amount) {
    log.info("Retrieving account for id {}", accountFromId);

    try {
      this.iTransferService.transferMoney(accountFromId, accountToId, amount);
    } catch (NegativeAmountException ex) {
      return ex.getMessage();
    }

    return "Amount " + amount +" transferred successfully";
  }

}
