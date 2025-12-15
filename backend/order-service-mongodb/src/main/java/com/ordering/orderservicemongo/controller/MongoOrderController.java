package com.ordering.orderservicemongo.controller;

import com.ordering.orderservicemongo.document.Order;
import com.ordering.orderservicemongo.service.MongoOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class MongoOrderController {
    @Autowired
    private MongoOrderService orderService;
    
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }
    
    @GetMapping("/{id}")
    public Order getOrder(@PathVariable String id) {
        return orderService.getOrderById(id);
    }
    
    @GetMapping("/user/{userId}")
    public List<Order> getUserOrders(@PathVariable Long userId) {
        return orderService.getOrdersByUserId(userId);
    }
    
    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        return orderService.updateOrderStatus(id, body.get("status"));
    }
}
