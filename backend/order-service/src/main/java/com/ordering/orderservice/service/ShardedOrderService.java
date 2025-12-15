package com.ordering.orderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordering.common.model.Cart;
import com.ordering.common.dto.OrderDTO;
import com.ordering.orderservice.client.CartClient;
import com.ordering.orderservice.entity.Order;
import com.ordering.orderservice.repository.ShardedOrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShardedOrderService {
    
    @Autowired
    private ShardedOrderRepository shardedOrderRepository;
    
    @Autowired
    private CartClient cartClient;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Transactional
    public OrderDTO createOrder(Long userId, String paymentMethod, String deliveryAddress) {
        Cart cart = cartClient.getCart(userId).getData();
        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(cart.getTotalAmount());
        order.setPaymentMethod(paymentMethod);
        order.setDeliveryAddress(deliveryAddress);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        try {
            order.setItems(objectMapper.writeValueAsString(cart.getItems()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize items");
        }
        
        // Save to appropriate shard based on userId
        Order saved = shardedOrderRepository.save(order);
        cartClient.clearCart(userId);
        
        rabbitTemplate.convertAndSend("order-exchange", "order.created", saved.getId());
        
        return convertToDTO(saved);
    }
    
    public OrderDTO getOrderById(Long orderId, Long userId) {
        Order order = shardedOrderRepository.findByIdAndUserId(orderId, userId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        return convertToDTO(order);
    }
    
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        return shardedOrderRepository.findByUserId(userId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<OrderDTO> getOrdersByStatus(String status) {
        return shardedOrderRepository.findByStatus(status).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public OrderDTO processPayment(Long orderId, Long userId) {
        Order order = shardedOrderRepository.findByIdAndUserId(orderId, userId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        
        order.setStatus("PAID");
        order.setUpdatedAt(LocalDateTime.now());
        Order updated = shardedOrderRepository.save(order);
        
        rabbitTemplate.convertAndSend("order-exchange", "order.payment", orderId);
        rabbitTemplate.convertAndSend("order-exchange", "order.notification", orderId);
        
        return convertToDTO(updated);
    }
    
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, Long userId, String status) {
        Order order = shardedOrderRepository.findByIdAndUserId(orderId, userId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        Order updated = shardedOrderRepository.save(order);
        
        return convertToDTO(updated);
    }
    
    public long getTotalOrderCount() {
        return shardedOrderRepository.getTotalOrderCount();
    }
    
    public List<ShardedOrderRepository.ShardStats> getShardStatistics() {
        return shardedOrderRepository.getShardStatistics();
    }
    
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }
}
