package com.example.rewards.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TransactionTest {

    @Test
    public void testTransactionConstructorAndGetters() {
        
        Customer customer = new Customer();
        customer.setCustomerId(1L);  
        customer.setCustomerName("John Doe");

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(200.0);
        transaction.setDate(LocalDate.parse("2025-01-01"));
        transaction.setCustomer(customer);  

   
        assertEquals(1L, transaction.getId());
        assertEquals(200.0, transaction.getAmount());
        assertEquals(LocalDate.parse("2025-01-01"), transaction.getDate());
        assertEquals(customer, transaction.getCustomer());
    }
}