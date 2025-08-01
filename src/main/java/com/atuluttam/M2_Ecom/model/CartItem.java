package com.atuluttam.M2_Ecom.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
private Product product;
private Integer quantity;
private BigDecimal price;

@CreationTimestamp
private LocalDateTime created;

@UpdateTimestamp
private LocalDateTime updated;

}
