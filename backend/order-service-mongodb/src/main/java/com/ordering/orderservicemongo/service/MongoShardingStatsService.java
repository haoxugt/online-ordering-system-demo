package com.ordering.orderservicemongo.service;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MongoShardingStatsService {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    public Map<String, Object> getShardingStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Get shard status
            Document shardStatus = mongoTemplate.executeCommand(new Document("shardingStatus", 1));
            
            stats.put("shardingEnabled", true);
            stats.put("shardingVersion", shardStatus.get("sharding version"));
            
            // Get shards info
            List<Map<String, Object>> shards = new ArrayList<>();
            Document shardsDoc = (Document) shardStatus.get("shards");
            if (shardsDoc != null) {
                shardsDoc.forEach((shardName, shardInfo) -> {
                    Map<String, Object> shard = new HashMap<>();
                    shard.put("name", shardName);
                    shard.put("info", shardInfo);
                    shards.add(shard);
                });
            }
            stats.put("shards", shards);
            
            // Get collection stats
            Document collStats = mongoTemplate.executeCommand(
                new Document("collStats", "orders").append("verbose", true)
            );
            
            stats.put("totalDocuments", collStats.get("count"));
            stats.put("totalSize", collStats.get("size"));
            stats.put("sharded", collStats.get("sharded"));
            
            // Get shard distribution
            Document shardDistribution = mongoTemplate.executeCommand(
                new Document("getShardDistribution", "orders")
            );
            stats.put("distribution", shardDistribution);
            
        } catch (Exception e) {
            // If not sharded (single node), return basic stats
            stats.put("shardingEnabled", false);
            stats.put("message", "Running on single MongoDB instance (not sharded)");
            stats.put("note", "For production, use sharded cluster with mongos routers");
            
            // Get basic collection stats
            try {
                Document collStats = mongoTemplate.executeCommand(
                    new Document("collStats", "orders")
                );
                stats.put("totalDocuments", collStats.get("count"));
                stats.put("totalSize", collStats.get("size"));
            } catch (Exception ex) {
                stats.put("totalDocuments", 0);
            }
        }
        
        return stats;
    }
    
    public Map<String, Object> getBalancerStatus() {
        Map<String, Object> balancer = new HashMap<>();
        
        try {
            Document balancerStatus = mongoTemplate.executeCommand(
                new Document("balancerStatus", 1)
            );
            
            balancer.put("mode", balancerStatus.get("mode"));
            balancer.put("inBalancerRound", balancerStatus.get("inBalancerRound"));
            balancer.put("numBalancerRounds", balancerStatus.get("numBalancerRounds"));
            
        } catch (Exception e) {
            balancer.put("error", "Not a sharded cluster or insufficient permissions");
        }
        
        return balancer;
    }
    
    public Map<String, Object> getChunkInfo() {
        Map<String, Object> chunks = new HashMap<>();
        
        try {
            // This requires admin privileges
            Document result = mongoTemplate.executeCommand(
                new Document("listShards", 1)
            );
            
            chunks.put("shards", result.get("shards"));
            
        } catch (Exception e) {
            chunks.put("message", "Chunk information requires admin access");
        }
        
        return chunks;
    }
}
