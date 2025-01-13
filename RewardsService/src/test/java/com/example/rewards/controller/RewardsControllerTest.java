package com.example.rewards.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.rewards.exception.DateFormatException;
import com.example.rewards.exception.ResourceNotFoundException;
import com.example.rewards.model.MonthlyRewards;
import com.example.rewards.model.RewardResponse;
import com.example.rewards.service.RewardService;

@WebMvcTest(RewardsController.class)
public class RewardsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RewardService rewardService;

	 @Test
	    public void testGetRewards_Success() throws Exception {
		 Long customerId = 1L;
	        RewardResponse mockResponse = new RewardResponse();
	        mockResponse.setCustomerId(customerId);
	        mockResponse.setCustomerName("Gauri Sharma");

	        Map<String, MonthlyRewards> monthlyRewards = new HashMap<>();
	        monthlyRewards.put("JANUARY", new MonthlyRewards(250, 200.0));
	        mockResponse.setMonthlyRewards(monthlyRewards);

	        when(rewardService.calculateRewards(customerId, "2025-01-10", "2025-02-05"))
	                .thenReturn(CompletableFuture.completedFuture(mockResponse));

	        MvcResult mvcResult = mockMvc.perform(get("/rewards/calculate")
	                .param("customerId", String.valueOf(customerId))
	                .param("startDate", "2025-01-10")
	                .param("endDate", "2025-02-05")
	                .accept(MediaType.APPLICATION_JSON))
	                .andExpect(request().asyncStarted())
	                .andReturn();

	        mockMvc.perform(asyncDispatch(mvcResult))
	                .andExpect(status().isOk())
	                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
	                .andExpect(jsonPath("$.customerId").value(customerId))
	                .andExpect(jsonPath("$.customerName").value("Gauri Sharma"))
	                .andExpect(jsonPath("$.monthlyRewards.JANUARY.totalPoints").value(250))
	                .andExpect(jsonPath("$.monthlyRewards.JANUARY.totalAmount").value(200.0));
	    }

	@Test
	public void testGetRewards_InvalidDateFormat() throws Exception {
		when(rewardService.calculateRewards(1L, "2025-01-35", "2025-02-01"))
				.thenThrow(new DateFormatException("Invalid date format. Please use 'yyyy-MM-dd'."));

		mockMvc.perform(MockMvcRequestBuilders.get("/rewards/calculate").param("customerId", "1")
				.param("startDate", "2025-01-35") // Invalid date
				.param("endDate", "2025-02-01").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message").value("Invalid date format. Please use 'yyyy-MM-dd'."))
				.andExpect(jsonPath("$.timestamp").exists())
				.andExpect(jsonPath("$.details").value("Please ensure the date format is 'yyyy-MM-dd'."));

		verify(rewardService, times(1)).calculateRewards(1L, "2025-01-35", "2025-02-01");
	}

	@Test
	public void testGetRewards_CustomerNotFound() throws Exception {

		when(rewardService.calculateRewards(1L, "2025-01-01", "2025-01-31"))
				.thenThrow(new ResourceNotFoundException("Customer", "1", "Customer not found."));

		mockMvc.perform(MockMvcRequestBuilders.get("/rewards/calculate").param("customerId", "1")
				.param("startDate", "2025-01-01").param("endDate", "2025-01-31")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.message").value("Customer not found."))
				.andExpect(jsonPath("$.timestamp").exists())
				.andExpect(jsonPath("$.details").value("The requested Customer with ID 1 could not be found."));

		verify(rewardService, times(1)).calculateRewards(1L, "2025-01-01", "2025-01-31");
	}

	@Test
	public void testGetRewards_TransactionNotFound() throws Exception {

		when(rewardService.calculateRewards(1L, "2025-01-01", "2025-01-31")).thenThrow(
				new ResourceNotFoundException("Transaction", "1", "No transactions found for the given date range."));

		mockMvc.perform(MockMvcRequestBuilders.get("/rewards/calculate").param("customerId", "1")
				.param("startDate", "2025-01-01").param("endDate", "2025-01-31")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.message").value("No transactions found for the given date range."))
				.andExpect(jsonPath("$.timestamp").exists())
				.andExpect(jsonPath("$.details").value("The requested Transaction with ID 1 could not be found."));

		verify(rewardService, times(1)).calculateRewards(1L, "2025-01-01", "2025-01-31");
	}

}
