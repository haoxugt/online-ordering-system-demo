package com.ordering.cartservice.client;
import com.ordering.common.dto.ApiResponse;
import com.ordering.common.dto.MenuItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(name = "menu-service")
public interface MenuClient {
    @GetMapping("/api/menu/items/{id}")
    ApiResponse<MenuItemDTO> getMenuItem(@PathVariable Long id);
}
