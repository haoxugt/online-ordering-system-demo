package com.ordering.cartservice.service;
import com.ordering.cartservice.client.MenuClient;
import com.ordering.cartservice.model.Cart;
import com.ordering.cartservice.model.CartItem;
import com.ordering.common.dto.MenuItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
@Service
public class CartService {
    @Autowired private RedisTemplate<String, Object> redisTemplate;
    @Autowired private MenuClient menuClient;
    
    private String getCartKey(Long userId) {
        return "cart:" + userId;
    }
    
    public Cart getCart(Long userId) {
        Cart cart = (Cart) redisTemplate.opsForValue().get(getCartKey(userId));
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
        }
        return cart;
    }
    
    public Cart addItem(Long userId, Long menuItemId, Integer quantity) {
        MenuItemDTO menuItem = menuClient.getMenuItem(menuItemId).getData();
        if (menuItem == null || !menuItem.isAvailable()) {
            throw new RuntimeException("Menu item not available");
        }
        
        Cart cart = getCart(userId);
        CartItem cartItem = new CartItem();
        cartItem.setMenuItemId(menuItemId);
        cartItem.setName(menuItem.getName());
        cartItem.setPrice(menuItem.getPrice());
        cartItem.setQuantity(quantity);
        cartItem.calculateSubtotal();
        
        cart.addItem(cartItem);
        saveCart(cart);
        return cart;
    }
    
    public Cart removeItem(Long userId, Long menuItemId) {
        Cart cart = getCart(userId);
        cart.removeItem(menuItemId);
        saveCart(cart);
        return cart;
    }
    
    public void clearCart(Long userId) {
        redisTemplate.delete(getCartKey(userId));
    }
    
    private void saveCart(Cart cart) {
        redisTemplate.opsForValue().set(getCartKey(cart.getUserId()), cart, 24, TimeUnit.HOURS);
    }
}
