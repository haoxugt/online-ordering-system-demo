package com.ordering.orderservice.controller;

import com.ordering.common.dto.ApiResponse;
import com.ordering.orderservice.repository.ShardedOrderRepository;
import com.ordering.orderservice.service.ShardedOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders/sharding")
public class ShardingStatsController {
    
    @Autowired
    private ShardedOrderService shardedOrderService;
    
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getShardingStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalOrders = shardedOrderService.getTotalOrderCount();
        List<ShardedOrderRepository.ShardStats> shardStats = shardedOrderService.getShardStatistics();
        
        stats.put("totalOrders", totalOrders);
        stats.put("numberOfShards", 4);
        stats.put("shardDetails", shardStats);
        stats.put("shardingStrategy", "Hash-based on user_id modulo 4");
        stats.put("partitioningStrategy", "Range partitioning by year on created_at");
        
        return ApiResponse.success(stats);
    }
}
