package com.example.bankapp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.bankapp.model.BankTransaction;
import com.example.bankapp.model.TransactionStatus;
import com.example.bankapp.repository.BankTransactionRepository;

//import java.math.BigDecimal;

/**
 * Saves a FAILED record after a transfer was rolled back.
 * REQUIRES_NEW = always start a brand-new, independent transaction,
 * so this record is committed even though the transfer itself was rolled back.
 */
@Service
public class TransactionAuditService {
        private final BankTransactionRepository transactionRepository;

    public TransactionAuditService(
            BankTransactionRepository transactionRepository) {

        this.transactionRepository = transactionRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailedTransaction(
            String sender,
            String receiver,
            double amount,
            String description) {

        BankTransaction transaction = new BankTransaction();

        transaction.setSenderAccountNumber(sender);
        transaction.setReceiverAccountNumber(receiver);
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setDescription(description);

        transactionRepository.save(transaction);
    }
        
}
