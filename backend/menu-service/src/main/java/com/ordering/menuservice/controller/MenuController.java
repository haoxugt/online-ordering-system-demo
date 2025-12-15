package com.ordering.menuservice.controller;
import com.ordering.common.dto.ApiResponse;
import com.ordering.common.dto.MenuItemDTO;
import com.ordering.menuservice.entity.MenuItem;
import com.ordering.menuservice.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/menu")
public class MenuController {
    @Autowired private MenuService menuService;
    
    @GetMapping("/items")
    public ApiResponse<List<MenuItemDTO>> getAllItems() {
        return ApiResponse.success(menuService.getAllMenuItems());
    }
    
    @GetMapping("/items/{id}")
    public ApiResponse<MenuItemDTO> getItem(@PathVariable Long id) {
        return ApiResponse.success(menuService.getMenuItemById(id));
    }
    
    @GetMapping("/items/category/{category}")
    public ApiResponse<List<MenuItemDTO>> getItemsByCategory(@PathVariable String category) {
        return ApiResponse.success(menuService.getMenuItemsByCategory(category));
    }
    
    @PostMapping("/items")
    public ApiResponse<MenuItemDTO> createItem(@RequestBody MenuItem menuItem) {
        return ApiResponse.success(menuService.createMenuItem(menuItem));
    }
    
    @PutMapping("/items/{id}")
    public ApiResponse<MenuItemDTO> updateItem(@PathVariable Long id, @RequestBody MenuItem menuItem) {
        return ApiResponse.success(menuService.updateMenuItem(id, menuItem));
    }
    
    @DeleteMapping("/items/{id}")
    public ApiResponse<Void> deleteItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ApiResponse.success(null);
    }
}
