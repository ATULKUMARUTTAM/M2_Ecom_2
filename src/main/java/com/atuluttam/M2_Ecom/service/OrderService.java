package com.atuluttam.M2_Ecom.service;

import com.atuluttam.M2_Ecom.dto.OrderItemDTO;
import com.atuluttam.M2_Ecom.dto.OrderResponse;
import com.atuluttam.M2_Ecom.model.*;
import com.atuluttam.M2_Ecom.repository.OrderRepository;
import com.atuluttam.M2_Ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class OrderService {


    private final CartService cartService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public Optional<OrderResponse> createOrder(String userId)
    {
        //Validate Cart items

        List<CartItem> cartItems = cartService.getCart(userId);

        if(cartItems.isEmpty())
        {
            return Optional.empty();
        }

        //Validate for user
        Optional<User> userOptional =  userRepository.findById(Long.valueOf(userId));
        if(userOptional.isEmpty())
        {
            return Optional.empty();
        }
        User user = userOptional.get();

        //Calculate total price
        BigDecimal totalPrice = cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create Order

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);

        List<OrderItems> orderItems = cartItems.stream()
                .map(item -> new OrderItems(
                        null,
                        item.getProduct(),
                        item.getQuantity(),
                        item.getPrice(),
                        order
                ))
                .toList();
        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        // Clear the cart
        cartService.clearCart(userId);

        return Optional.of(mapToOrderResponse(savedOrder));
    }


//    public Optional<OrderResponse> createOrder(String userId) {
//        // Get cart items for the user
//        List<CartItem> cartItems = cartService.getCart(userId);
//
//        // Check if cart is empty
//        if (cartItems.isEmpty()) {
//            return Optional.empty();
//        }
//
//        // Verify user exists
//        Optional<User> userOptional = userRepository.findById(Long.valueOf(userId));
//        if (userOptional.isEmpty()) {
//            return Optional.empty();
//        }
//        User user = userOptional.get();
//
//        // Calculate total price of cart items
//        BigDecimal totalPrice = BigDecimal.ZERO;
//        for (CartItem item : cartItems) {
//            totalPrice = totalPrice.add(item.getPrice());
//        }
//
//        // Create and populate new order
//        Order order = new Order();
//        order.setUser(user);
//        order.setStatus(OrderStatus.CONFIRMED);
//        order.setTotalAmount(totalPrice);
//
//        // Create order items from cart items
//        List<OrderItems> orderItems = new ArrayList<>();
//        for (CartItem item : cartItems) {
//            OrderItems orderItem = new OrderItems(null, item.getProduct(), item.getQuantity(), item.getPrice(), order);
//            orderItems.add(orderItem);
//        }
//        order.setItems(orderItems);
//
//        // Save order to database
//        Order savedOrder = orderRepository.save(order);
//
//        // Clear user's cart
//        cartService.clearCart(userId);
//
//        // Return mapped order response
//        return Optional.of(mapToOrderResponse(savedOrder));
//    }

    private OrderResponse mapToOrderResponse(Order order) {

        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getItems().stream()
                        .map(orderItem -> new OrderItemDTO(
                              orderItem.getId(),
                                orderItem.getProduct().getId(),
                                orderItem.getQuantity(),
                                orderItem.getPrice(),
                                orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))
                        ))
                        .toList(),
                order.getCreatedAt()
        );
    }

//    private OrderResponse mapToOrderResponse(Order order) {
//        // Create a list to store OrderItemDTOs
//        List<OrderItemDTO> orderItemDTOs = new ArrayList<>();
//
//        // Convert each OrderItem to OrderItemDTO
//        for (OrderItems orderItem : order.getItems()) {
//            BigDecimal totalItemPrice = orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()));
//            OrderItemDTO itemDTO = new OrderItemDTO(
//                    orderItem.getId(),
//                    orderItem.getProduct().getId(),
//                    orderItem.getQuantity(),
//                    orderItem.getPrice(),
//                    totalItemPrice
//            );
//            orderItemDTOs.add(itemDTO);
//        }
//
//        // Create and return the OrderResponse with the list of OrderItemDTOs
//        return new OrderResponse(
//                order.getId(),
//                order.getTotalAmount(),
//                order.getStatus(),
//                orderItemDTOs,
//                order.getCreatedAt()
//        );
//    }




}
