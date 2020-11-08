package com.homechallenge.checkout.dto;

import com.homechallenge.checkout.model.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutDetailDTO {

    private List<Product> items;

    private BigDecimal totalPrice;

    private BigDecimal totalSaved;
}
