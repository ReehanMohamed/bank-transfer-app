package com.example.bankapp.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.bankapp.dto.TransferRequest;
import com.example.bankapp.model.Customer;
import com.example.bankapp.repository.BankTransactionRepository;
import com.example.bankapp.repository.CustomerRepository;

@Controller
public class HomeController {

    private final CustomerRepository customerRepository;
    private final BankTransactionRepository transactionRepository;

    public HomeController(
            CustomerRepository customerRepository,
            BankTransactionRepository transactionRepository) {
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(defaultValue = "") String keyword,
            Model model) {
        List<Customer> allCustomers = customerRepository.findAllOrdered();
        List<Customer> customers = keyword.isBlank()
                ? allCustomers
                : customerRepository.searchByName(keyword);

        model.addAttribute("totalCustomers", customerRepository.countAccounts());
        model.addAttribute("totalBalance", customerRepository.getTotalBalance());
        model.addAttribute("successCount", transactionRepository.countSuccessfulTransactions());
        model.addAttribute("failedCount", transactionRepository.countFailedTransactions());
        model.addAttribute("allCustomers", allCustomers);
        model.addAttribute("customers", customers);
        model.addAttribute("transactions", transactionRepository.findAllOrdered());
        model.addAttribute("transferRequest", new TransferRequest());
        model.addAttribute("customer", new Customer());
        model.addAttribute("keyword", keyword);
        return "bank";
    }
}
