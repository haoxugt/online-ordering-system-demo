package com.ordering.orderservicemongo.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
@Data
public class Order {
    @Id
    private String id;
    @Field("user_id")
    private Long userId;
    private List<OrderItem> items;
    @Field("total_amount")
    private BigDecimal totalAmount;
    private String status = "PENDING";
    @Field("payment_method")
    private String paymentMethod;
    @Field("delivery_address")
    private String deliveryAddress;
    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    @Field("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Data
    public static class OrderItem {
        @Field("menu_item_id")
        private Long menuItemId;
        private String name;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subtotal;
    }
}
