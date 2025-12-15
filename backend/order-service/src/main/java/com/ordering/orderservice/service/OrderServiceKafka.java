package com.ordering.orderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordering.common.model.Cart;
import com.ordering.common.dto.OrderDTO;
import com.ordering.orderservice.client.CartClient;
import com.ordering.orderservice.entity.Order;
import com.ordering.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.ordering.orderservice.config.KafkaConfig.*;

@Service
@Profile("kafka")
public class OrderServiceKafka {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CartClient cartClient;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
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
        
        try {
            order.setItems(objectMapper.writeValueAsString(cart.getItems()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize items");
        }
        
        Order saved = orderRepository.save(order);
        cartClient.clearCart(userId);
        
        // Send to Kafka topic
        kafkaTemplate.send(ORDER_CREATED_TOPIC, saved.getId().toString());
        
        return convertToDTO(saved);
    }
    
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToDTO(order);
    }
    
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public OrderDTO processPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus("PAID");
        order.setUpdatedAt(LocalDateTime.now());
        Order updated = orderRepository.save(order);
        
        // Send payment event to Kafka
        kafkaTemplate.send(ORDER_PAYMENT_TOPIC, orderId.toString());
        
        // Send notification event to Kafka
        kafkaTemplate.send(ORDER_NOTIFICATION_TOPIC, orderId.toString());
        
        return convertToDTO(updated);
    }
    
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        Order updated = orderRepository.save(order);
        
        return convertToDTO(updated);
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
