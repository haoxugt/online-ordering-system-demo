package com.ordering.orderservice.client;
import com.ordering.common.model.Cart;
import com.ordering.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(name = "cart-service")
public interface CartClient {
    @GetMapping("/api/cart/{userId}")
    ApiResponse<Cart> getCart(@PathVariable Long userId);
    
    @DeleteMapping("/api/cart/{userId}")
    ApiResponse<Void> clearCart(@PathVariable Long userId);
}
