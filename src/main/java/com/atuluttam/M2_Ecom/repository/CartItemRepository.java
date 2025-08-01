package com.atuluttam.M2_Ecom.repository;

import com.atuluttam.M2_Ecom.model.CartItem;
import com.atuluttam.M2_Ecom.model.Product;
import com.atuluttam.M2_Ecom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByUserAndProduct(User user, Product product);

    void deleteByUserAndProduct(User user, Product product);

    List<CartItem> findByUser(User user);

    void deleteByUser(User user);
}
