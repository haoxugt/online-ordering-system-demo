package com.ordering.orderservicemongo.repository;

import com.ordering.orderservicemongo.document.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MongoOrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(Long userId);
    List<Order> findByStatus(String status);
}
