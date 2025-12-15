package com.ordering.cartservice.controller;
import com.ordering.cartservice.model.Cart;
import com.ordering.cartservice.service.CartService;
import com.ordering.common.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired private CartService cartService;
    
    @GetMapping("/{userId}")
    public ApiResponse<Cart> getCart(@PathVariable Long userId) {
        return ApiResponse.success(cartService.getCart(userId));
    }
    
    @PostMapping("/{userId}/items")
    public ApiResponse<Cart> addItem(@PathVariable Long userId, @RequestBody Map<String, Object> request) {
        Long menuItemId = ((Number) request.get("menuItemId")).longValue();
        Integer quantity = (Integer) request.get("quantity");
        return ApiResponse.success(cartService.addItem(userId, menuItemId, quantity));
    }
    
    @DeleteMapping("/{userId}/items/{menuItemId}")
    public ApiResponse<Cart> removeItem(@PathVariable Long userId, @PathVariable Long menuItemId) {
        return ApiResponse.success(cartService.removeItem(userId, menuItemId));
    }
    
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ApiResponse.success(null);
    }
}
