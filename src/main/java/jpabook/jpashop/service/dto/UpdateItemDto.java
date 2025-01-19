package jpabook.jpashop.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateItemDto {
    private String name;
    private int price;
    private int stockQuantity;
}
