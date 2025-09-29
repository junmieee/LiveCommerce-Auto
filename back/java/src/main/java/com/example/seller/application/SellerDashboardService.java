package com.example.seller.application;

import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.repository.OrderItemRepository;
import com.example.order.repository.OrderRepository;
import com.example.product.domain.Product;
import com.example.product.domain.ProductRepository;
import com.example.seller.domain.SellerMembershipChecker;
import com.example.seller.presentation.dto.SellerManagementResponse;
import com.example.seller.presentation.dto.SellerOrdersResponse;
import com.example.seller.presentation.dto.SellerShippingsResponse;
import com.example.shipping.entity.Shipping;
import com.example.shipping.repository.ShippingRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class SellerDashboardService {

  private final SellerMembershipChecker sellerMembershipChecker;
  private final ProductRepository productRepository;
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final ShippingRepository shippingRepository;

  public SellerDashboardService(
      SellerMembershipChecker sellerMembershipChecker,
      ProductRepository productRepository,
      OrderRepository orderRepository,
      OrderItemRepository orderItemRepository,
      ShippingRepository shippingRepository) {
    this.sellerMembershipChecker = sellerMembershipChecker;
    this.productRepository = productRepository;
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
    this.shippingRepository = shippingRepository;
  }

  public SellerManagementResponse management(Long sellerId, Long userId) {
    assertMembership(sellerId, userId);

    long totalProducts = productRepository.countBySellerId(sellerId);
    long activeProducts = productRepository.countBySellerIdAndIsActiveTrue(sellerId);
    long inactiveProducts = totalProducts - activeProducts;
    Long totalStock = productRepository.sumStockQuantityBySellerId(sellerId);

    List<Product> recentProducts =
        productRepository.findTop5BySellerIdOrderByUpdatedAtDesc(sellerId);

    List<SellerManagementResponse.ProductSummary> summaries =
        recentProducts.stream()
            .map(
                product ->
                    new SellerManagementResponse.ProductSummary(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getStockQuantity(),
                        product.getIsActive(),
                        product.getUpdatedAt()))
            .collect(Collectors.toList());

    return new SellerManagementResponse(
        true,
        totalProducts,
        activeProducts,
        inactiveProducts,
        totalStock == null ? 0L : totalStock,
        summaries);
  }

  public SellerOrdersResponse orders(Long sellerId, Long userId) {
    assertMembership(sellerId, userId);

    List<Order> orders = orderRepository.findAllBySellerId(sellerId);
    if (orders.isEmpty()) {
      return SellerOrdersResponse.empty();
    }

    List<Long> orderIds = orders.stream().map(Order::getId).toList();
    List<OrderItem> orderItems = orderItemRepository.findByOrderIdIn(orderIds);

    Map<Long, List<OrderItem>> itemsByOrder =
        orderItems.stream().collect(Collectors.groupingBy(OrderItem::getOrderId));

    Set<Long> productIds =
        orderItems.stream()
            .map(OrderItem::getProductId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    Map<Long, Product> productMap =
        productIds.isEmpty()
            ? Collections.emptyMap()
            : productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

    List<SellerOrdersResponse.OrderSummary> summaries =
        orders.stream()
            .sorted(
                Comparator.comparing(
                        Order::getOrderedAt, Comparator.nullsLast(LocalDateTime::compareTo))
                    .reversed())
            .map(
                order -> {
                  List<OrderItem> items = itemsByOrder.getOrDefault(order.getId(), List.of());
                  List<SellerOrdersResponse.OrderItemSummary> itemSummaries =
                      items.stream()
                          .map(
                              item -> {
                                Product product = productMap.get(item.getProductId());
                                return new SellerOrdersResponse.OrderItemSummary(
                                    item.getId(),
                                    item.getProductId(),
                                    product != null ? product.getName() : null,
                                    parseQuantity(item.getQuantity()),
                                    item.getUnitPrice(),
                                    item.getTotalPrice());
                              })
                          .collect(Collectors.toList());
                  return new SellerOrdersResponse.OrderSummary(
                      order.getId(),
                      order.getUserId(),
                      order.getTotalPrice(),
                      order.getOrderStatus(),
                      order.getOrderedAt(),
                      order.getConfirmedAt(),
                      order.getCancelledAt(),
                      itemSummaries);
                })
            .collect(Collectors.toList());

    Map<String, Long> statusCounts =
        summaries.stream()
            .collect(
                Collectors.groupingBy(
                    SellerOrdersResponse.OrderSummary::getOrderStatus, Collectors.counting()));

    return new SellerOrdersResponse(true, summaries.size(), statusCounts, summaries);
  }

  public SellerShippingsResponse shippings(Long sellerId, Long userId) {
    assertMembership(sellerId, userId);

    List<Shipping> shippings = shippingRepository.findAllBySellerId(sellerId);
    if (shippings.isEmpty()) {
      return SellerShippingsResponse.empty();
    }

    List<SellerShippingsResponse.ShippingSummary> summaries =
        shippings.stream()
            .sorted(
                Comparator.comparing(
                        Shipping::getShippedAt, Comparator.nullsLast(LocalDateTime::compareTo))
                    .reversed())
            .map(
                shipping ->
                    new SellerShippingsResponse.ShippingSummary(
                        shipping.getId(),
                        shipping.getOrderId(),
                        shipping.getShippingStatus(),
                        shipping.getShippedAt(),
                        shipping.getDeliveredAt(),
                        shipping.getRecipientName(),
                        shipping.getPhoneNumber(),
                        shipping.getTrackingNumber(),
                        shipping.getCarrier(),
                        shipping.getAddress()))
            .collect(Collectors.toList());

    Map<String, Long> statusCounts =
        summaries.stream()
            .collect(
                Collectors.groupingBy(
                    SellerShippingsResponse.ShippingSummary::getShippingStatus,
                    Collectors.counting()));

    return new SellerShippingsResponse(true, summaries.size(), statusCounts, summaries);
  }

  private void assertMembership(Long sellerId, Long userId) {
    if (!sellerMembershipChecker.isMemberOfSeller(sellerId, userId)) {
      throw new AccessDeniedException("해당 판매자에 대한 권한이 없습니다.");
    }
  }

  private Integer parseQuantity(String quantity) {
    if (quantity == null) {
      return null;
    }
    try {
      return Integer.valueOf(quantity);
    } catch (NumberFormatException ex) {
      return null;
    }
  }
}
