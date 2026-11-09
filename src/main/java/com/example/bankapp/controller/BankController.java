package com.example.bankapp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bankapp.dto.TransferRequest;
import com.example.bankapp.model.BankTransaction;
import com.example.bankapp.model.Customer;
import com.example.bankapp.repository.BankTransactionRepository;
import com.example.bankapp.repository.CustomerRepository;
import com.example.bankapp.service.BankService;
import com.example.bankapp.service.TransactionAuditService;
@RestController
@RequestMapping("/bank")
public class BankController {
    private final BankService bankService;
    private final TransactionAuditService auditService;
    private final CustomerRepository customerRepository;
    private final BankTransactionRepository transactionRepository;

    public BankController(
            BankService bankService,
            TransactionAuditService auditService,
            CustomerRepository customerRepository,
            BankTransactionRepository transactionRepository) {

        this.bankService = bankService;
        this.auditService = auditService;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    // 1. Transfer money
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(
            @RequestBody TransferRequest request) {

        try {

            bankService.transfer(request);

            return ResponseEntity.ok(
                    Map.of("message", "Transfer successful")
            );

        } catch (RuntimeException e) {

            // Save failed transaction independently
            auditService.saveFailedTransaction(
                    request.getSenderAccountNumber(),
                    request.getReceiverAccountNumber(),
                    request.getAmount(),
                    e.getMessage()
            );

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "FAILED",
                            "message", e.getMessage()
                    )
            );
        }
    }

    // 2. Get all customers
    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAllOrdered();
    }

    // 3. Get account history
    @GetMapping("/history/{accountNumber}")
    public List<BankTransaction> getAccountHistory(
            @PathVariable String accountNumber) {

        return transactionRepository.findAccountHistory(accountNumber);
    }

    // 4. Get all transactions
    @GetMapping("/transactions")
    public List<BankTransaction> getAllTransactions() {
        return transactionRepository.findAllOrdered();
    }

    // 5. Dashboard statistics
    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {

        return Map.of(
                "totalCustomers", customerRepository.countAccounts(),
                "totalBalance", customerRepository.getTotalBalance(),
                "successfulTransactions",
                transactionRepository.countSuccessfulTransactions(),
                "failedTransactions",
                transactionRepository.countFailedTransactions()
        );
    }
}
