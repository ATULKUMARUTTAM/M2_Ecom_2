package com.atuluttam.M2_Ecom.repository;

import com.atuluttam.M2_Ecom.dto.ProductResponse;
import com.atuluttam.M2_Ecom.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stockQunatity >0 AND LOWER(p.name) LIKE LOWER(CONCAT('%' , :keyword, '%'))")
    List<Product> searchproduct(@Param("keyword") String keyword);
}
