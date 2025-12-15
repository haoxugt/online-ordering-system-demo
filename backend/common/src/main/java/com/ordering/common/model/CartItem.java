package com.ordering.common.model;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CartItem implements Serializable {
    private Long menuItemId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
    
    public void calculateSubtotal() {
        this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
    }
}
