package com.anant.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderListItemsDto {
    private Long id;
    private String skucode;
    private BigDecimal price;
    private Integer quantity;
}
