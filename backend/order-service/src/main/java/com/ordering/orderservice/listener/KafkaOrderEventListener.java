package com.ordering.orderservice.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("kafka")
public class KafkaOrderEventListener {
    
    @KafkaListener(
        topics = "order-created",
        groupId = "order-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderCreated(
            @Payload String orderId,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        log.info("Received order created event: orderId={}, partition={}, offset={}", 
                 orderId, partition, offset);
        
        try {
            // Process order created event
            processOrderCreated(Long.parseLong(orderId));
            
            // Acknowledge message
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing order created event", e);
            // Message will be retried
        }
    }
    
    @KafkaListener(
        topics = "order-payment",
        groupId = "order-service-group"
    )
    public void handleOrderPayment(@Payload String orderId, Acknowledgment acknowledgment) {
        log.info("Processing payment for order: {}", orderId);
        
        try {
            // Process payment
            processPayment(Long.parseLong(orderId));
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing payment event", e);
        }
    }
    
    @KafkaListener(
        topics = "order-notification",
        groupId = "order-service-group"
    )
    public void handleOrderNotification(@Payload String orderId, Acknowledgment acknowledgment) {
        log.info("Sending notification for order: {}", orderId);
        
        try {
            // Send notification (email, SMS, push)
            sendNotification(Long.parseLong(orderId));
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Error sending notification", e);
        }
    }
    
    private void processOrderCreated(Long orderId) {
        // Business logic for order creation
        log.info("Order {} created successfully", orderId);
        // Update analytics, send to warehouse, etc.
    }
    
    private void processPayment(Long orderId) {
        // Business logic for payment processing
        log.info("Payment processed for order {}", orderId);
        // Update inventory, trigger fulfillment, etc.
    }
    
    private void sendNotification(Long orderId) {
        // Business logic for notifications
        log.info("Notification sent for order {}", orderId);
        // Send email, SMS, push notification, etc.
    }
}
