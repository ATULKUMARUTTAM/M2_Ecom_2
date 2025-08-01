package com.atuluttam.M2_Ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
public class OrderItemDTO {
      private Long Id;
      private Long productId;
      private Integer quantity;
      private BigDecimal price;
      private BigDecimal subTotal;

}
