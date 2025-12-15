package com.ordering.common.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CartItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long menuItemId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
