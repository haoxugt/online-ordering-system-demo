package com.ordering.orderservice.config;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RabbitMQConfig {
    public static final String ORDER_EXCHANGE = "order-exchange";
    public static final String ORDER_QUEUE = "order-queue";
    public static final String PAYMENT_QUEUE = "payment-queue";
    public static final String NOTIFICATION_QUEUE = "notification-queue";
    
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }
    
    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE, true);
    }
    
    @Bean
    public Queue paymentQueue() {
        return new Queue(PAYMENT_QUEUE, true);
    }
    
    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }
    
    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with("order.created");
    }
    
    @Bean
    public Binding paymentBinding() {
        return BindingBuilder.bind(paymentQueue()).to(orderExchange()).with("order.payment");
    }
    
    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue()).to(orderExchange()).with("order.notification");
    }
}
