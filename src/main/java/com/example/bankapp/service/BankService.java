package com.example.bankapp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bankapp.dto.TransferRequest;
import com.example.bankapp.exception.AccountNotFoundException;
import com.example.bankapp.exception.BankException;
import com.example.bankapp.exception.InsufficientBalanceException;
import com.example.bankapp.model.BankTransaction;
import com.example.bankapp.model.TransactionStatus;
import com.example.bankapp.repository.BankTransactionRepository;
import com.example.bankapp.repository.CustomerRepository;

@Service
@Transactional(readOnly = true)          // default for every method: read-only transaction
public class BankService {

     private final CustomerRepository customerRepository;
    private final BankTransactionRepository transactionRepository;

    public BankService(
            CustomerRepository customerRepository,
            BankTransactionRepository transactionRepository) {

        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void transfer(TransferRequest request) {

        String sender = request.getSenderAccountNumber();
        String receiver = request.getReceiverAccountNumber();
        double amount = request.getAmount();

        // 1. Validate amount
        if (amount <= 0) {
            throw new BankException(
                    "Transfer amount must be greater than zero"
            );
        }

        // 2. Prevent transferring to same account
        if (sender.equals(receiver)) {
            throw new BankException(
                    "Sender and receiver accounts cannot be the same"
            );
        }

        // 3. Check sender account
        customerRepository.findByAccountNumber(sender)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Sender account not found"
                ));

        // 4. Check receiver account
        customerRepository.findByAccountNumber(receiver)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Receiver account not found"
                ));

        // 5. Debit sender
        int debited = customerRepository.debit(sender, amount);

        if (debited == 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance"
            );
        }

        // 6. Credit receiver
        int credited = customerRepository.credit(receiver, amount);

        if (credited == 0) {
            throw new AccountNotFoundException(
                    "Receiver account not found"
            );
        }

        // 7. Create successful transaction record
        BankTransaction transaction = new BankTransaction();

        transaction.setSenderAccountNumber(sender);
        transaction.setReceiverAccountNumber(receiver);
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription("Money transferred successfully");

        transactionRepository.save(transaction);
    }

}
