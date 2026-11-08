package com.example.bankapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bankapp.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    // 1. Get customer by account number
    @Query("SELECT c FROM Customer c WHERE c.accountNumber = :accountNumber")
    Optional<Customer> getByAccountNumber(
            @Param("accountNumber") String accountNumber
    );

    // 2. Count all accounts
    @Query("SELECT COUNT(c) FROM Customer c")
    long countAccounts();

    // 3. Count customers by email
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.email = :email")
    long countEmails(@Param("email") String email);

    // 4. Find all customers ordered by name
    @Query("SELECT c FROM Customer c ORDER BY c.name ASC")
    List<Customer> findAllOrdered();

    // 5. Search customer by name
    @Query("SELECT c FROM Customer c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Customer> searchByName(@Param("name") String name);

    // 6. Get total balance
    @Query("SELECT COALESCE(SUM(c.balance), 0) FROM Customer c")
    double getTotalBalance();

    // 7. Native SQL query
    @Query(value = "SELECT COUNT(*) FROM customers", nativeQuery = true)
    long countCustomersNative();

    // 8. Debit amount from sender
    @Modifying
    @Query("""
            UPDATE Customer c
            SET c.balance = c.balance - :amount
            WHERE c.accountNumber = :accountNumber
            AND c.balance >= :amount
            """)
    int debit(
            @Param("accountNumber") String accountNumber,
            @Param("amount") double amount
    );

    // 9. Credit amount to receiver
    @Modifying
    @Query("""
            UPDATE Customer c
            SET c.balance = c.balance + :amount
            WHERE c.accountNumber = :accountNumber
            """)
    int credit(
            @Param("accountNumber") String accountNumber,
            @Param("amount") double amount
    );

}
