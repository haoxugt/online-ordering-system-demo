package com.ordering.orderservice.listener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
public class OrderEventListener {
    
    @RabbitListener(queues = "order-queue")
    public void handleOrderCreated(Long orderId) {
        log.info("Order created: {}", orderId);
    }
    
    @RabbitListener(queues = "payment-queue")
    public void handlePayment(Long orderId) {
        log.info("Processing payment for order: {}", orderId);
    }
    
    @RabbitListener(queues = "notification-queue")
    public void handleNotification(Long orderId) {
        log.info("Sending notification for order: {}", orderId);
    }
}
