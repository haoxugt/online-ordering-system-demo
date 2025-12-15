package com.ordering.common.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class MenuItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private boolean available;
}
