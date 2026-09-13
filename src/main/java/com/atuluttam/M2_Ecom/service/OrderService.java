package com.atuluttam.M2_Ecom.service;

import com.atuluttam.M2_Ecom.dto.OrderItemDTO;
import com.atuluttam.M2_Ecom.dto.OrderResponse;
import com.atuluttam.M2_Ecom.model.*;
import com.atuluttam.M2_Ecom.repository.OrderRepository;
import com.atuluttam.M2_Ecom.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Optional<OrderResponse> createOrder(String userId) {

        // 1. Get cart items
        List<CartItem> cartItems = cartService.getCart(userId);

        if (cartItems.isEmpty()) {
            return Optional.empty();
        }

        // 2. Find user
        Optional<User> userOptional =
                userRepository.findById(Long.valueOf(userId));

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();

        // 3. Calculate total amount
        BigDecimal totalPrice = BigDecimal.ZERO;

        List<OrderItems> orderItems = new ArrayList<>();

        // 4. Process every cart item
        for (CartItem item : cartItems) {

            Product product = item.getProduct();

            int quantity = item.getQuantity();

            // Check stock again
            if (product.getStockQunatity() < quantity) {
                throw new RuntimeException(
                        "Insufficient stock for product: "
                                + product.getName()
                );
            }

            // -----------------------------------------
            // REDUCE PRODUCT STOCK
            // -----------------------------------------
            product.setStockQunatity(
                    product.getStockQunatity() - quantity
            );

            // Calculate item price
            BigDecimal itemTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(quantity));

            totalPrice = totalPrice.add(itemTotal);

            // Create OrderItems
            OrderItems orderItem = new OrderItems(
                    null,
                    product,
                    quantity,
                    product.getPrice(),   // UNIT PRICE
                    null
            );

            orderItems.add(orderItem);
        }

        // 5. Create Order
        Order order = new Order();

        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);

        // Set order reference in every OrderItems
        for (OrderItems item : orderItems) {
            item.setOrder(order);
        }

        order.setItems(orderItems);

        // 6. Save order
        Order savedOrder = orderRepository.save(order);

        // 7. Clear cart
        cartService.clearCart(userId);

        // 8. Return response
        return Optional.of(mapToOrderResponse(savedOrder));
    }


    private OrderResponse mapToOrderResponse(Order order) {

        List<OrderItemDTO> orderItemDTOs = new ArrayList<>();

        for (OrderItems orderItem : order.getItems()) {

            BigDecimal subTotal =
                    orderItem.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            orderItem.getQuantity()
                                    )
                            );

            OrderItemDTO itemDTO = new OrderItemDTO(
                    orderItem.getId(),
                    orderItem.getProduct().getId(),
                    orderItem.getQuantity(),
                    orderItem.getPrice(),
                    subTotal
            );

            orderItemDTOs.add(itemDTO);
        }

        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                orderItemDTOs,
                order.getCreatedAt()
        );
    }
}
