package com.example.order.repository;

import com.example.order.entity.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

  @Query(
      "SELECT DISTINCT o FROM Order o WHERE o.id IN ("
          + "SELECT oi.orderId FROM OrderItem oi JOIN Product p ON p.id = oi.productId "
          + "WHERE p.sellerId = :sellerId)")
  List<Order> findAllBySellerId(@Param("sellerId") Long sellerId);
}
