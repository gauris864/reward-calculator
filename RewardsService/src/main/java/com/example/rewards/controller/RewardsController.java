package com.example.rewards.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rewards.model.RewardResponse;
import com.example.rewards.service.RewardService;

import java.util.concurrent.CompletableFuture;

@RestController
public class RewardsController {

    private static final Logger logger = LoggerFactory.getLogger(RewardsController.class);

    @Autowired
    private RewardService rewardService;

    @GetMapping("/rewards/calculate")
    public CompletableFuture<ResponseEntity<?>> getRewards(
            @RequestParam Long customerId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        long startTime = System.currentTimeMillis();
        logger.info("Received request to calculate rewards for customer ID: {}", customerId);

        CompletableFuture<RewardResponse> rewardResponseFuture = rewardService.calculateRewards(customerId, startDate, endDate);

        return rewardResponseFuture.thenApply(rewardResponse -> {
            long endTime = System.currentTimeMillis();
            logger.info("Total execution time for reward calculation: {} ms", (endTime - startTime));
            return ResponseEntity.ok(rewardResponse);
        });
    }
}
