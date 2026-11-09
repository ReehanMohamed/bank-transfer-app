package com.example.bankapp.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.bankapp.repository.BankTransactionRepository;
import com.example.bankapp.repository.CustomerRepository;

class HomeControllerTest {

    @Test
    void rootDisplaysBankDashboard() throws Exception {
        CustomerRepository customers = mock(CustomerRepository.class);
        BankTransactionRepository transactions = mock(BankTransactionRepository.class);
        when(customers.findAllOrdered()).thenReturn(List.of());
        when(customers.searchByName("")).thenReturn(List.of());
        when(transactions.findAllOrdered()).thenReturn(List.of());

        MockMvc mvc = MockMvcBuilders
                .standaloneSetup(new HomeController(customers, transactions))
                .build();

        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("bank"))
                .andExpect(model().attributeExists(
                        "customers", "allCustomers", "transactions", "transferRequest", "customer"));
    }
}
