package com.atuluttam.M2_Ecom.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQunatity;
    private String category;
    private String imageUrl;

}
