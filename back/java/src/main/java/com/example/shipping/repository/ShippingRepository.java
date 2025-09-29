package com.example.shipping.repository;

import com.example.shipping.entity.Shipping;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShippingRepository extends JpaRepository<Shipping, Long> {

  @Query(
      "SELECT DISTINCT s FROM Shipping s WHERE s.orderId IN ("
          + "SELECT oi.orderId FROM OrderItem oi JOIN Product p ON p.id = oi.productId "
          + "WHERE p.sellerId = :sellerId)")
  List<Shipping> findAllBySellerId(@Param("sellerId") Long sellerId);
}
