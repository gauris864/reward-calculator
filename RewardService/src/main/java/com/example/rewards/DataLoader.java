package com.example.rewards;

import com.example.rewards.model.Customer;
import com.example.rewards.model.Transaction;
import com.example.rewards.repository.CustomerRepository;
import com.example.rewards.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@PostConstruct
	public void loadData() {
		loadCustomers();
		loadTransactions();
	}

	private void loadCustomers() {
		// Create sample customers
		Customer customer1 = new Customer(1L, "Gauri Sharma");
		Customer customer2 = new Customer(2L, "Prashasti Mishra");
		Customer customer3 = new Customer(3L, "Kratika Saxena");

		customerRepository.saveAll(Arrays.asList(customer1, customer2,customer3));
	}

	private void loadTransactions() {
		
		Transaction transaction1 = new Transaction(1L, 120.0, LocalDate.of(2025, 1, 15),
				new Customer(1L, "Gauri Sharma"));
		Transaction transaction2 = new Transaction(2L, 80.0, LocalDate.of(2025, 1, 20),
				new Customer(1L, "Gauri Sharma"));
		Transaction transaction3 = new Transaction(3L, 150.0, LocalDate.of(2025, 2, 5),
				new Customer(1L, "Gauri Sharma"));
		Transaction transaction4 = new Transaction(4L, 100.0, LocalDate.of(2025, 2, 10),
				new Customer(1L, "Gauri Sharma"));
		Transaction transaction5 = new Transaction(5L, 200.0, LocalDate.of(2025, 3, 12),
				new Customer(1L, "Gauri Sharma"));
		Transaction transaction6 = new Transaction(6L, 50.0, LocalDate.of(2025, 3, 18),
				new Customer(1L, "Gauri Sharma"));

		Transaction transaction7 = new Transaction(7L, 200.0, LocalDate.of(2025, 1, 10),
				new Customer(2L, "Prashasti Mishra"));
		Transaction transaction8 = new Transaction(8L, 90.0, LocalDate.of(2025, 2, 15),
				new Customer(2L, "Prashasti Mishra"));
		Transaction transaction9 = new Transaction(9L, 110.0, LocalDate.of(2025, 3, 20),
				new Customer(2L, "Prashasti Mishra"));

		Transaction transaction10 = new Transaction(7L, 200.0, LocalDate.of(2025, 1, 10),
				new Customer(3L, "Kratika Saxena"));
		Transaction transaction11 = new Transaction(8L, 90.0, LocalDate.of(2025, 2, 15),
				new Customer(3L, "Kratika Saxena"));
		Transaction transactio12 = new Transaction(9L, 110.0, LocalDate.of(2025, 3, 20),
				new Customer(3L, "Kratika Saxena"));

		transactionRepository.saveAll(
				Arrays.asList(transaction1, transaction2, transaction3, transaction4, transaction5, transaction6,
						transaction7, transaction8, transaction9,transaction10,transaction11,transactio12));
	}
}