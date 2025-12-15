package com.ordering.menuservice.repository;
import com.ordering.menuservice.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByCategory(String category);
    List<MenuItem> findByAvailable(boolean available);
}
