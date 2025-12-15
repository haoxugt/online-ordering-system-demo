package com.ordering.common.model;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class Cart implements Serializable {
    private Long userId;
    private List<CartItem> items = new ArrayList<>();
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    public void addItem(CartItem item) {
        items.removeIf(i -> i.getMenuItemId().equals(item.getMenuItemId()));
        items.add(item);
        calculateTotal();
    }
    
    public void removeItem(Long menuItemId) {
        items.removeIf(i -> i.getMenuItemId().equals(menuItemId));
        calculateTotal();
    }
    
    public void clear() {
        items.clear();
        totalAmount = BigDecimal.ZERO;
    }
    
    private void calculateTotal() {
        totalAmount = items.stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
