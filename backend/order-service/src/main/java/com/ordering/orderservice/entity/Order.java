package com.ordering.orderservice.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String items;
    @Column(nullable = false)
    private BigDecimal totalAmount;
    @Column(nullable = false)
    private String status = "PENDING";
    private String paymentMethod;
    private String deliveryAddress;

    //Required for partitioning
    @Column(name = "created_year", nullable = false)
    private Integer createdYear;


    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Auto-set the year
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.createdYear = this.createdAt.getYear();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
