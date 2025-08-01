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
        Optional<Product> productOptional = productRepository.findById(request.getProductId());
    if(productOptional.isEmpty())
        return false;
    Product product = productOptional.get();

    if(product.getStockQunatity() < request.getQuantity())
        return false;
    Optional<User> userOptional = userRepository.findById(Long.valueOf(userId));
    if(userOptional.isEmpty())
        return false;

    User user = userOptional.get();
    CartItem existingCartItem  = cartItemRepository.findByUserAndProduct(user, product);
    if(existingCartItem !=null)
    {
        existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
        existingCartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
        cartItemRepository.save(existingCartItem);
    }
    else {
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            cartItemRepository.save(cartItem);
    }
    return true;
    }

    public boolean deleteItemfromCart(String userId, Long productId) {

        Optional<Product> productOptional = productRepository.findById(productId);
        Optional<User> userOptional = userRepository.findById(Long.valueOf(userId));
       if(productOptional.isPresent() && userOptional.isPresent()) {
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

    public void clearCart(String userId) {
        userRepository.findById(Long.valueOf(userId)).ifPresent(
                cartItemRepository::deleteByUser);
    }
}
