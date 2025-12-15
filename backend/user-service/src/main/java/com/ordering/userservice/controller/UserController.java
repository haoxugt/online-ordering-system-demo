package com.ordering.userservice.controller;
import com.ordering.common.dto.ApiResponse;
import com.ordering.common.dto.UserDTO;
import com.ordering.userservice.entity.User;
import com.ordering.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired private UserService userService;
    
    @PostMapping("/register")
    public ApiResponse<UserDTO> register(@RequestBody User user) {
        return ApiResponse.success(userService.register(user));
    }
    
    @PostMapping("/login")
    public ApiResponse<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String token = userService.login(credentials.get("username"), credentials.get("password"));
        return ApiResponse.success(Map.of("token", token));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<UserDTO> getUser(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id));
    }
}
