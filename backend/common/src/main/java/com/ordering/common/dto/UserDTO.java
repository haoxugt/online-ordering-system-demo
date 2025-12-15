package com.ordering.common.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String role;
}
