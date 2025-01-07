package com.example.rewards.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class RewardResponse {
	
	@JsonProperty("customerId")
	private Long customerId;
	@JsonProperty("customerName")
	private String customerName;
	@JsonProperty("monthlyRewards")
	private Map<String, Integer> monthlyRewards;
	private List<Transaction> transactions;

}
