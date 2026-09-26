package com.atuluttam.M2_Ecom.service;

import com.atuluttam.M2_Ecom.dto.CartItemRequest;
import com.atuluttam.M2_Ecom.model.CartItem;
import com.atuluttam.M2_Ecom.model.Product;
import com.atuluttam.M2_Ecom.model.User;
import com.atuluttam.M2_Ecom.repository.CartItemRepository;
import com.atuluttam.M2_Ecom.repository.ProductRepository;
import com.atuluttam.M2_Ecom.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    public boolean addToCart(String userId, CartItemRequest request)
    {

        // 1. Find product
        Optional<Product> productOptional = productRepository.findById(request.getProductId());

        if (productOptional.isEmpty()) {
            return false;
        }

        Product product = productOptional.get();

        // 2. Check whether product is active
        if (!product.getActive()) {
            return false;
        }

        // 3. Check requested quantity
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            return false;
        }

        // 4. Find user
        Optional<User> userOptional =
                userRepository.findById(Long.valueOf(userId));

        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();

        // 5. Check whether product already exists in user's cart
        CartItem existingCartItem =
                cartItemRepository.findByUserAndProduct(user, product);

        // 6. Calculate new total quantity
        int newQuantity = request.getQuantity();

        if (existingCartItem != null) {
            newQuantity =
                    existingCartItem.getQuantity()
                            + request.getQuantity();
        }

        // 7. Check stock
        if (newQuantity > product.getStockQunatity()) {
            return false;
        }

        // 8. If product already exists in cart
        if (existingCartItem != null) {

            existingCartItem.setQuantity(newQuantity);

            // Store total price of all items
            existingCartItem.setPrice(
                    product.getPrice()
                            .multiply(BigDecimal.valueOf(newQuantity))
            );

            cartItemRepository.save(existingCartItem);
        }

        // 9. Otherwise create new cart item
        else {

            CartItem cartItem = new CartItem();

            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());

            // Total price = product price × quantity
            cartItem.setPrice(
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(request.getQuantity())
                            )
            );

            cartItemRepository.save(cartItem);
        }

        return true;
    }

    public boolean deleteItemfromCart(String userId, Long productId) {

        Optional<Product> productOptional = productRepository.findById(productId);
        Optional<User> userOptional = userRepository.findById(Long.valueOf(userId));
       if(productOptional.isPresent() && userOptional.isPresent())
       {
           cartItemRepository.deleteByUserAndProduct(userOptional.get(), productOptional.get());
           return true;
       }
        return false;
    }

    public List<CartItem> getCart(String userId) {

        return userRepository.findById(Long.valueOf(userId))
                .map(cartItemRepository::findByUser)
                .orElseGet(List::of);
    }


//    public List<CartItem> getCart(String userId) {
//        // Find user by ID
//        Optional<User> userOptional = userRepository.findById(Long.valueOf(userId));
//
//        // If user is not found, return an empty list
//        if (userOptional.isEmpty()) {
//            return new ArrayList<>();
//        }
//
//        // Get cart items for the user
//        User user = userOptional.get();
//        return cartItemRepository.findByUser(user);
//    }

    public void clearCart(String userId) {
        userRepository.findById(Long.valueOf(userId)).ifPresent(
                cartItemRepository::deleteByUser);
    }

//
//    public void clearCart(String userId) {
//        // Find user by ID
//        Optional<User> userOptional = userRepository.findById(Long.valueOf(userId));
//
//        // If user exists, delete their cart items
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            cartItemRepository.deleteByUser(user);
//        }
//    }
}
