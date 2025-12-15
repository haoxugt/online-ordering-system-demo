package com.ordering.orderservicemongo.controller;

import com.ordering.common.dto.ApiResponse;
import com.ordering.orderservicemongo.service.MongoShardingStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/orders/mongo-sharding")
public class MongoShardingStatsController {
    
    @Autowired
    private MongoShardingStatsService shardingStatsService;
    
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getShardingStats() {
        Map<String, Object> stats = shardingStatsService.getShardingStats();
        return ApiResponse.success(stats);
    }
    
    @GetMapping("/balancer")
    public ApiResponse<Map<String, Object>> getBalancerStatus() {
        Map<String, Object> balancer = shardingStatsService.getBalancerStatus();
        return ApiResponse.success(balancer);
    }
    
    @GetMapping("/chunks")
    public ApiResponse<Map<String, Object>> getChunkInfo() {
        Map<String, Object> chunks = shardingStatsService.getChunkInfo();
        return ApiResponse.success(chunks);
    }
}
