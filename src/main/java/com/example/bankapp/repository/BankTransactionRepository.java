package com.example.bankapp.repository;
import com.example.bankapp.model.BankTransaction;
import com.example.bankapp.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bankapp.model.BankTransaction;
import java.util.List;
public interface BankTransactionRepository
        extends JpaRepository<BankTransaction, Long> {

                // Get all transactions ordered by date
    @Query("SELECT t FROM BankTransaction t ORDER BY t.createdAt DESC")
    List<BankTransaction> findAllOrdered();

    // Get transactions for a particular account
    @Query("""
            SELECT t FROM BankTransaction t
            WHERE t.senderAccountNumber = :account
            OR t.receiverAccountNumber = :account
            ORDER BY t.createdAt DESC
            """)
    List<BankTransaction> findAccountHistory(
            @Param("account") String account
    );

    // Get failed transactions
    @Query("""
            SELECT t FROM BankTransaction t
            WHERE t.status = :status
            ORDER BY t.createdAt DESC
            """)
    List<BankTransaction> findByStatus(
            @Param("status") TransactionStatus status
    );

    // Count successful transactions
    @Query("""
            SELECT COUNT(t) FROM BankTransaction t
            WHERE t.status = 'SUCCESS'
            """)
    long countSuccessfulTransactions();

    // Count failed transactions
    @Query("""
            SELECT COUNT(t) FROM BankTransaction t
            WHERE t.status = 'FAILED'
            """)
    long countFailedTransactions();
}