package com.ordering.orderservice.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShardingConfig {
    
    @Value("${spring.datasource.username}")
    private String username;
    
    @Value("${spring.datasource.password}")
    private String password;
    
    @Value("${spring.datasource.base-url:jdbc:mysql://localhost:3306}")
    private String baseUrl;
    
    private static final int NUM_SHARDS = 4;
    
    /**
     * Create datasources for all order database shards
     */
    @Bean
    public Map<Integer, DataSource> shardDataSources() {
        Map<Integer, DataSource> dataSources = new HashMap<>();
        
        for (int i = 0; i < NUM_SHARDS; i++) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(baseUrl + "/order_db_" + i + "?createDatabaseIfNotExist=true");
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            
            dataSources.put(i, new HikariDataSource(config));
        }
        
        return dataSources;
    }
    
    /**
     * Primary datasource for default operations (shard 0)
     */
    @Primary
    @Bean(name = "dataSource")
    public DataSource primaryDataSource() {
        return shardDataSources().get(0);
    }
    
    /**
     * Determine which shard to use based on user_id
     * Shard key: user_id % NUM_SHARDS
     */
    public int getShardIndex(Long userId) {
        return (int) (userId % NUM_SHARDS);
    }
    
    /**
     * Get datasource for specific shard
     */
    public DataSource getDataSourceForShard(int shardIndex) {
        return shardDataSources().get(shardIndex);
    }
    
    /**
     * Get datasource based on user_id
     */
    public DataSource getDataSourceForUser(Long userId) {
        int shardIndex = getShardIndex(userId);
        return getDataSourceForShard(shardIndex);
    }
}
