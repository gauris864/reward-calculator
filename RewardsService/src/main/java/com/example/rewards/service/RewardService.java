package com.example.rewards.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.rewards.controller.RewardsController;
import com.example.rewards.exception.DateFormatException;
import com.example.rewards.exception.ResourceNotFoundException;
import com.example.rewards.model.Customer;
import com.example.rewards.model.RewardResponse;
import com.example.rewards.model.Transaction;
import com.example.rewards.repository.CustomerRepository;
import com.example.rewards.repository.TransactionRepository;

@Service
public class RewardService {
	
	private static final Logger logger = LoggerFactory.getLogger(RewardService.class);

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private TransactionRepository transactionRepository;
    
    @Async("taskExecutor")
    public CompletableFuture<RewardResponse> calculateRewards(Long customerId, String startDate, String endDate) {
    	
    	logger.info("Starting reward calculation for customerId: {}, startDate: {}, endDate: {}", customerId, startDate, endDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start;
        LocalDate end;

        try {
            start = LocalDate.parse(startDate, formatter);
            end = LocalDate.parse(endDate, formatter);
            logger.info("Parsed dates successfully - Start: {}, End: {}", start, end);
        } catch (Exception e) {
        	logger.error("Date parsing failed for startDate: {}, endDate: {}", startDate, endDate, e);
            throw new DateFormatException("Invalid date format. Please use 'yyyy-MM-dd'.");
        }
        

        
        Optional<Customer> customerExistOrNot = customerRepo.findById(customerId);
        if (!customerExistOrNot.isPresent()) {
        	logger.error("Customer not found for customerId: {}", customerId);
        	throw new ResourceNotFoundException("Customer", customerId.toString(), "Customer not found.");
        }
        

        List<Transaction> transactions = transactionRepository.findByCustomer_CustomerId(customerId);

        List<Transaction> filteredTransactions = transactions.stream()
                .filter(transaction -> !transaction.getDate().isBefore(start) && !transaction.getDate().isAfter(end))
                .collect(Collectors.toList());

       
        if (filteredTransactions.isEmpty()) {
        	logger.error("Transaction not found for customerId: {} in the date range of {} to {}",customerId.toString(),startDate,endDate);
        	throw new ResourceNotFoundException("Transaction", customerId.toString(), "No transactions found for the given customer.");
        }
        logger.info("Found {} transactions for customerId: {} in the date range: {} to {}", filteredTransactions.size(), customerId, start, end);

        Map<String, Integer> rewards = new HashMap<>();
        for (Transaction trans : filteredTransactions) {
            int points = calculatePoints(trans.getAmount());
            String month = trans.getDate().getMonth().toString();
            rewards.put(month, rewards.getOrDefault(month, 0) + points);
            logger.debug("Transaction amount: {}, Points awarded: {}, Month: {}", trans.getAmount(), points, month);
        }

        
        RewardResponse response = new RewardResponse();
        response.setCustomerId(customerId);
        response.setCustomerName(customerExistOrNot.get().getCustomerName());
        response.setMonthlyRewards(rewards);
        response.setTransactions(filteredTransactions);
        
        
        logger.info("Reward calculation completed for customerId: {}", customerId);
        logger.debug("Reward response: {}", response);
        
        return CompletableFuture.completedFuture(response);
    }

    private int calculatePoints(double amount) {
        int points = 0;
        if (amount > 100) {
            points += (amount - 100) * 2;
            points += 50; 
        } else if (amount > 50) {
            points += (amount - 50) * 1;
        }
        logger.debug("Points calculated for amount {}: {}", amount, points);
        return points;
        
    }
}
