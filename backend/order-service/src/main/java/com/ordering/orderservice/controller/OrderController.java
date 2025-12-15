package com.ordering.orderservice.controller;
import com.ordering.common.dto.ApiResponse;
import com.ordering.common.dto.OrderDTO;
import com.ordering.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired private OrderService orderService;
    
    @PostMapping
    public ApiResponse<OrderDTO> createOrder(@RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        String paymentMethod = (String) request.get("paymentMethod");
        String deliveryAddress = (String) request.get("deliveryAddress");
        return ApiResponse.success(orderService.createOrder(userId, paymentMethod, deliveryAddress));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<OrderDTO> getOrder(@PathVariable Long id) {
        return ApiResponse.success(orderService.getOrderById(id));
    }
    
    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderDTO>> getUserOrders(@PathVariable Long userId) {
        return ApiResponse.success(orderService.getOrdersByUserId(userId));
    }
    
    @PostMapping("/{id}/payment")
    public ApiResponse<OrderDTO> processPayment(@PathVariable Long id) {
        return ApiResponse.success(orderService.processPayment(id));
    }
    
    @PutMapping("/{id}/status")
    public ApiResponse<OrderDTO> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        return ApiResponse.success(orderService.updateOrderStatus(id, request.get("status")));
    }
}
