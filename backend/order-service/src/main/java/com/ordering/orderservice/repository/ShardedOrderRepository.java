package com.ordering.orderservice.repository;

import com.ordering.orderservice.config.ShardingConfig;
import com.ordering.orderservice.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ShardedOrderRepository {
    
    @Autowired
    private ShardingConfig shardingConfig;
    
    private final RowMapper<Order> orderRowMapper = new RowMapper<Order>() {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setId(rs.getLong("id"));
            order.setUserId(rs.getLong("user_id"));
            order.setItems(rs.getString("items"));
            order.setTotalAmount(rs.getBigDecimal("total_amount"));
            order.setStatus(rs.getString("status"));
            order.setPaymentMethod(rs.getString("payment_method"));
            order.setDeliveryAddress(rs.getString("delivery_address"));
            order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            order.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return order;
        }
    };
    
    /**
     * Save order to appropriate shard based on user_id
     */
    public Order save(Order order) {
        DataSource dataSource = shardingConfig.getDataSourceForUser(order.getUserId());
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        if (order.getId() == null) {
            // Insert
            String sql = "INSERT INTO orders (user_id, items, total_amount, status, payment_method, delivery_address, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            jdbcTemplate.update(sql, 
                order.getUserId(),
                order.getItems(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentMethod(),
                order.getDeliveryAddress(),
                Timestamp.valueOf(order.getCreatedAt()),
                Timestamp.valueOf(order.getUpdatedAt())
            );
            
            // Get generated ID
            Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
            order.setId(id);
        } else {
            // Update
            String sql = "UPDATE orders SET status = ?, payment_method = ?, delivery_address = ?, updated_at = ? " +
                        "WHERE id = ? AND user_id = ?";
            
            jdbcTemplate.update(sql,
                order.getStatus(),
                order.getPaymentMethod(),
                order.getDeliveryAddress(),
                Timestamp.valueOf(order.getUpdatedAt()),
                order.getId(),
                order.getUserId()
            );
        }
        
        return order;
    }
    
    /**
     * Find order by ID - requires userId to determine shard
     */
    public Order findByIdAndUserId(Long orderId, Long userId) {
        DataSource dataSource = shardingConfig.getDataSourceForUser(userId);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        String sql = "SELECT * FROM orders WHERE id = ? AND user_id = ?";
        
        List<Order> orders = jdbcTemplate.query(sql, orderRowMapper, orderId, userId);
        return orders.isEmpty() ? null : orders.get(0);
    }
    
    /**
     * Find all orders for a user - queries specific shard
     */
    public List<Order> findByUserId(Long userId) {
        DataSource dataSource = shardingConfig.getDataSourceForUser(userId);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";
        
        return jdbcTemplate.query(sql, orderRowMapper, userId);
    }
    
    /**
     * Find orders by status - queries ALL shards (scatter-gather)
     */
    public List<Order> findByStatus(String status) {
        List<Order> allOrders = new ArrayList<>();
        
        // Query all shards
        for (int i = 0; i < 4; i++) {
            DataSource dataSource = shardingConfig.getDataSourceForShard(i);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            
            String sql = "SELECT * FROM orders WHERE status = ? ORDER BY created_at DESC";
            List<Order> shardOrders = jdbcTemplate.query(sql, orderRowMapper, status);
            allOrders.addAll(shardOrders);
        }
        
        // Sort combined results
        allOrders.sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));
        
        return allOrders;
    }
    
    /**
     * Get total order count across all shards
     */
    public long getTotalOrderCount() {
        long totalCount = 0;
        
        for (int i = 0; i < 4; i++) {
            DataSource dataSource = shardingConfig.getDataSourceForShard(i);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            
            Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Long.class);
            totalCount += (count != null ? count : 0);
        }
        
        return totalCount;
    }
    
    /**
     * Get shard statistics
     */
    public List<ShardStats> getShardStatistics() {
        List<ShardStats> stats = new ArrayList<>();
        
        for (int i = 0; i < 4; i++) {
            DataSource dataSource = shardingConfig.getDataSourceForShard(i);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            
            Long orderCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Long.class);
            
            ShardStats shardStats = new ShardStats();
            shardStats.setShardIndex(i);
            shardStats.setOrderCount(orderCount != null ? orderCount : 0);
            shardStats.setDatabaseName("order_db_" + i);
            
            stats.add(shardStats);
        }
        
        return stats;
    }
    
    public static class ShardStats {
        private int shardIndex;
        private String databaseName;
        private long orderCount;
        
        public int getShardIndex() { return shardIndex; }
        public void setShardIndex(int shardIndex) { this.shardIndex = shardIndex; }
        
        public String getDatabaseName() { return databaseName; }
        public void setDatabaseName(String databaseName) { this.databaseName = databaseName; }
        
        public long getOrderCount() { return orderCount; }
        public void setOrderCount(long orderCount) { this.orderCount = orderCount; }
    }
}
