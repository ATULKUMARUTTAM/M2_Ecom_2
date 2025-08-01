package com.atuluttam.M2_Ecom.dto;


import lombok.Data;

@Data
public class CartItemRequest {
    private Long productId;
    private Integer quantity;
}
