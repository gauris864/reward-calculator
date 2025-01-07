package com.example.rewards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.rewards.exception.DateFormatException;
import com.example.rewards.exception.ResourceNotFoundException;
import com.example.rewards.model.Customer;
import com.example.rewards.model.RewardResponse;
import com.example.rewards.model.Transaction;
import com.example.rewards.repository.CustomerRepository;
import com.example.rewards.repository.TransactionRepository;

public class RewardServiceTest {
	
	@Mock
	private CustomerRepository customerRepository;
	@Mock
	private TransactionRepository transactionRepository;
	@InjectMocks
	private RewardService rewardService;
	
	private Customer customer;
	private Transaction transaction1;
    private Transaction transaction2;
    
	@BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
        customer = new Customer(1L, "Gauri Sharma");

        transaction1 = new Transaction(1L, 120.0, LocalDate.parse("2025-01-15"), customer);
        transaction2 = new Transaction(2L, 80.0, LocalDate.parse("2025-01-20"), customer);
    }
	
	@Test
	public void testCalculateRewards() throws InterruptedException, ExecutionException {
		
		when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomer_CustomerId(1L)).thenReturn(Arrays.asList(transaction1, transaction2));

        CompletableFuture<RewardResponse> future = rewardService.calculateRewards(1L, "2025-01-01", "2025-01-31");
        RewardResponse response = future.get();
        assertNotNull(response);
        assertEquals(1L, response.getCustomerId());
        assertEquals("Gauri Sharma", response.getCustomerName());
        assertNotNull(response.getMonthlyRewards());
        assertTrue(response.getMonthlyRewards().containsKey("JANUARY"));
        assertEquals(120, response.getMonthlyRewards().get("JANUARY"));
        //assertEquals(1, response.getTransactions().size());
        assertEquals(transaction1.getId(), response.getTransactions().get(0).getId());

	}
	@Test
	public void testCalculateRewards_InvalidDateFormat() {
		DateFormatException exception = assertThrows(DateFormatException.class, () -> {
		    rewardService.calculateRewards(1L, "2025-01-32", "2025-02-01");
		});
		assertEquals("Invalid date format. Please use 'yyyy-MM-dd'.", exception.getMessage());
	}
	
	@Test
	public void testCalculateRewards_CustomerNotFound() {
		
		when(customerRepository.findById(1L)).thenReturn(Optional.empty());
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
		    rewardService.calculateRewards(1L, "2025-01-01", "2025-02-01");
		});
		assertEquals("Customer not found.", exception.getMessage());
	}
	
	@Test
	public void testCalculateRewards_NoTransactionsFound() {
		when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomer_CustomerId(1L)).thenReturn(Arrays.asList());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            rewardService.calculateRewards(1L, "2025-01-01", "2025-01-31");
        });
        assertEquals("No transactions found for the given customer.", exception.getMessage());

	}
}
