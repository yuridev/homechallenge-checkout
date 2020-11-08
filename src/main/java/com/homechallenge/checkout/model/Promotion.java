package com.homechallenge.checkout.model;

import com.homechallenge.checkout.enums.PromotionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Promotion {

    public static final BigDecimal DECIMAL_FACTOR = BigDecimal.valueOf(0.01);

    private String id;
    private PromotionType type;
    private Integer required_qty;
    private BigDecimal price;
    private Integer free_qty;
    private BigDecimal amount;

    public BigDecimal calculateTotalPrice(List<Product> products) {

        BigDecimal productPrice = products.stream().findFirst().map(Product::getPrice).orElse(BigDecimal.ZERO);

        BigDecimal size = BigDecimal.valueOf(products.size());

        if(PromotionType.QTY_BASED_PRICE_OVERRIDE.equals(type)) {
            if(products.size() < required_qty) {
                return productPrice.multiply(size).setScale(2, RoundingMode.CEILING);
            }

            int mod = products.size() % required_qty;
            int quantityAllowedPromotion = Math.floorDiv(products.size(), required_qty);
            BigDecimal promotionTotalCost = price.multiply(BigDecimal.valueOf(quantityAllowedPromotion));
            BigDecimal totalPriceMod = productPrice.multiply(BigDecimal.valueOf(mod));

            if(mod == 0) {
                return promotionTotalCost.setScale(2, RoundingMode.CEILING);
            }
            return promotionTotalCost.add(totalPriceMod).setScale(2, RoundingMode.CEILING);
        }

        if(PromotionType.BUY_X_GET_Y_FREE.equals(type)) {
            if(products.size() < required_qty) {
                return productPrice.multiply(size).setScale(2, RoundingMode.CEILING);
            }

            int mod = products.size() % required_qty;
            BigDecimal promotionTotalCost = productPrice.multiply(
                    BigDecimal.valueOf((products.size() / required_qty) * free_qty));
            BigDecimal totalPriceMod = productPrice.multiply(BigDecimal.valueOf(mod));

            if(mod == 0) {
                return promotionTotalCost.setScale(2, RoundingMode.CEILING);
            }
            return promotionTotalCost.add(totalPriceMod).setScale(2, RoundingMode.CEILING);
        }

        if(PromotionType.FLAT_PERCENT.equals(type)) {
            return productPrice.subtract(productPrice.multiply(amount.multiply(DECIMAL_FACTOR))).multiply(size).setScale(2, RoundingMode.CEILING);
        }

        return BigDecimal.ZERO;
    }
}
