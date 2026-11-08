package com.example.bankapp.config;
import com.example.bankapp.model.Customer;
import com.example.bankapp.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.bankapp.model.Customer;
import com.example.bankapp.repository.CustomerRepository;

//import java.math.BigDecimal;
//import java.util.List;

/** Inserts three sample customers the first time the application starts (empty database). */
@Component
public class DataLoader implements CommandLineRunner {

    private final CustomerRepository customerRepository;

    public DataLoader(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void run(String... args) {

        if (!customerRepository.existsByAccountNumber("ACC1001")) {

            customerRepository.save(
                    new Customer(
                            "ACC1001",
                            "Reehan",
                            "reehan@gmail.com",
                            10000
                    )
            );
        }

        if (!customerRepository.existsByAccountNumber("ACC1002")) {

            customerRepository.save(
                    new Customer(
                            "ACC1002",
                            "Mohamed",
                            "mohamed@gmail.com",
                            5000
                    )
            );
        }

        if (!customerRepository.existsByAccountNumber("ACC1003")) {

            customerRepository.save(
                    new Customer(
                            "ACC1003",
                            "Kamal",
                            "kamal@gmail.com",
                            8000
                    )
            );
        }

        System.out.println("Sample customers loaded successfully!");
    }
}