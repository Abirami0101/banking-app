package net.javaguides.banking.service.impl;

import net.javaguides.banking.dto.AccountDto;
import net.javaguides.banking.dto.AccountTransferDto;
import net.javaguides.banking.dto.TransactionDto;
import net.javaguides.banking.entity.Account;
import net.javaguides.banking.entity.Transaction;
import net.javaguides.banking.exception.AccountException;
import net.javaguides.banking.mapper.AccountMapper;
import net.javaguides.banking.repository.AccountRepository;
import net.javaguides.banking.repository.TransactionRepository;
import net.javaguides.banking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {
@Autowired
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private static final String TRANSACTION_TYPE_DEPOSIT="DEPOSIT";
    private static final String TRANSACTION_TYPE_WITHDRAW="WITHDRAW";
    private static final String TRANSACTION_TYPE_TRANSFER="TRANSFER";


    public AccountServiceImpl(AccountRepository accountRepository,
                              TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository=transactionRepository;
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        Account account = AccountMapper.mapToAccount(accountDto);
        Account savedAccount=accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto getAccountById(Long id) {
        Account account=accountRepository.findById(id)
                .orElseThrow(() -> new AccountException("Account Id Does Not Exist"));
        return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public AccountDto deposit(Long id, double amount) {
        Account account=accountRepository.findById(id)
                .orElseThrow(() -> new AccountException("Account Id Does Not Exist"));

        double total=account.getBalance()+amount;
        account.setBalance(total);
        Account savedAccount= accountRepository.save(account);

        Transaction transaction=new Transaction();
                transaction.setAccountId(id);
                transaction.setTransactionType(TRANSACTION_TYPE_DEPOSIT);
                transaction.setTimestamp(LocalDateTime.now());
                transaction.setAmount(amount);
        transactionRepository.save(transaction);

        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto withdraw(Long id, double amount) {
        Account account=accountRepository.findById(id)
                .orElseThrow(() -> new AccountException("Account Id Does Not Exist"));
        if(account.getBalance()<amount){
            throw new RuntimeException("Insufficient Balance");
        }
        double total=account.getBalance()-amount;
        account.setBalance(total);
        Account savedAccount=accountRepository.save(account);

        Transaction transaction=new Transaction();
        transaction.setAccountId(id);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setTransactionType(TRANSACTION_TYPE_WITHDRAW);

        transactionRepository.save(transaction);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        List<Account> accounts=accountRepository.findAll();

        return accounts.stream()
                .map((account -> AccountMapper.mapToAccountDto(account)))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAccount(Long id) {
        Account account=accountRepository.findById(id)
                .orElseThrow(() -> new AccountException("Account Id Does Not Exist"));
        accountRepository.deleteById(id);
    }

    @Override
    public void transferFund(AccountTransferDto accountTransferDto) {
        //id for fromAccount
        Account fromAccount=accountRepository.findById(accountTransferDto.fromAccountId())
                .orElseThrow(()-> new AccountException("Account id does not exist"));
        //id for toAccount
        Account toAccount=accountRepository.findById(accountTransferDto.toAccountId())
                .orElseThrow(()->new AccountException("Account id does not exist"));

        if(accountTransferDto.amount()>fromAccount.getBalance()){
            throw new RuntimeException("Insufficient Balance");
        }
        fromAccount.setBalance(fromAccount.getBalance()-accountTransferDto.amount());

        toAccount.setBalance(toAccount.getBalance()+accountTransferDto.amount());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction=new Transaction();
        transaction.setAccountId(accountTransferDto.fromAccountId());
        transaction.setAmount(accountTransferDto.amount());
        transaction.setTransactionType(TRANSACTION_TYPE_TRANSFER);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    @Override
    public List<TransactionDto> getTransactionByAccountId(Long accountId) {
        List<Transaction> transactions=transactionRepository
                .findByAccountIdOrderByTimestampDesc(accountId);

        return transactions.stream()
                .map((transaction -> AccountMapper.mapToTransactionDto(transaction)))
                .collect(Collectors.toList());
    }


}
