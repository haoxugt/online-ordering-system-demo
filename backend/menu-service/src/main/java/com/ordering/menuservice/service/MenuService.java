package com.ordering.menuservice.service;
import com.ordering.common.dto.MenuItemDTO;
import com.ordering.menuservice.entity.MenuItem;
import com.ordering.menuservice.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class MenuService {
    @Autowired private MenuItemRepository menuItemRepository;
    
    @Cacheable(value = "menuItems", key = "'all'")
    public List<MenuItemDTO> getAllMenuItems() {
        return menuItemRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Cacheable(value = "menuItems", key = "#id")
    public MenuItemDTO getMenuItemById(Long id) {
        MenuItem item = menuItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Menu item not found"));
        return convertToDTO(item);
    }
    
    public List<MenuItemDTO> getMenuItemsByCategory(String category) {
        return menuItemRepository.findByCategory(category).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @CacheEvict(value = "menuItems", allEntries = true)
    public MenuItemDTO createMenuItem(MenuItem menuItem) {
        MenuItem saved = menuItemRepository.save(menuItem);
        return convertToDTO(saved);
    }
    
    @CacheEvict(value = "menuItems", allEntries = true)
    public MenuItemDTO updateMenuItem(Long id, MenuItem menuItem) {
        MenuItem existing = menuItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Menu item not found"));
        existing.setName(menuItem.getName());
        existing.setDescription(menuItem.getDescription());
        existing.setPrice(menuItem.getPrice());
        existing.setCategory(menuItem.getCategory());
        existing.setImageUrl(menuItem.getImageUrl());
        existing.setAvailable(menuItem.isAvailable());
        existing.setUpdatedAt(LocalDateTime.now());
        MenuItem updated = menuItemRepository.save(existing);
        return convertToDTO(updated);
    }
    
    @CacheEvict(value = "menuItems", allEntries = true)
    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }
    
    private MenuItemDTO convertToDTO(MenuItem item) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setCategory(item.getCategory());
        dto.setImageUrl(item.getImageUrl());
        dto.setAvailable(item.isAvailable());
        return dto;
    }
}
