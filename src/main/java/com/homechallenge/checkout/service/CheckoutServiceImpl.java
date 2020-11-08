package com.homechallenge.checkout.service;

import com.homechallenge.checkout.dto.CheckoutDetailDTO;
import com.homechallenge.checkout.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    @Override
    public CheckoutDetailDTO checkout(List<Product> products) {
        return calculate(products);
    }

    @Override
    public void addItem(List<Product> products, Product product) {
        if (Objects.nonNull(products) && Objects.nonNull(product)) {
            products.add(product);
        }
    }

    private CheckoutDetailDTO calculate(List<Product> products) {
        List<BigDecimal> pricesWithPromotion = new ArrayList<>();
        Map<String, List<Product>> productsByName = products.stream().collect(Collectors.groupingBy(Product::getId));
        productsByName.forEach((id, productsGrouped) -> {
            Product product = productsGrouped.stream().findFirst().get();
            if(CollectionUtils.isEmpty(product.getPromotions())) {
                pricesWithPromotion.add(product.getPrice());
            }
            product.getPromotions().forEach(promotion -> pricesWithPromotion.add(promotion.calculateTotalPrice(productsGrouped)));
        });

        BigDecimal total = products.stream().map(Product::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCost = pricesWithPromotion.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CheckoutDetailDTO(products, total, total.subtract(totalCost));
    }
}
