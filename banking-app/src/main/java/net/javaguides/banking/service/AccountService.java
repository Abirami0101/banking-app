package net.javaguides.banking.service;

import net.javaguides.banking.dto.AccountDto;
import net.javaguides.banking.dto.AccountTransferDto;
import net.javaguides.banking.dto.TransactionDto;
import net.javaguides.banking.entity.Account;

import java.util.List;

public interface AccountService {
     AccountDto createAccount(AccountDto accountDto);

     AccountDto getAccountById(Long id);

     AccountDto deposit(Long id, double amount);

     AccountDto withdraw(Long id, double amount);

     List<AccountDto> getAllAccounts();

     void deleteAccount(Long id);

     void transferFund(AccountTransferDto accountTransferDto);
     List<TransactionDto> getTransactionByAccountId(Long accountId);
}
