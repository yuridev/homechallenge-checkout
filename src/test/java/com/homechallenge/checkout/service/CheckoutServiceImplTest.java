package com.homechallenge.checkout.service;

import com.homechallenge.checkout.dto.CheckoutDetailDTO;
import com.homechallenge.checkout.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
class CheckoutServiceImplTest {

    public static final BigDecimal DECIMAL_FACTOR = BigDecimal.valueOf(0.01);
    @Autowired
    private CheckoutService checkoutService;

    Product pizza;
    Product burger;
    Product salad;
    Product fries;

    @BeforeEach
    public void setUp() {
        String apiUrl = "http://localhost:8081/products/%s";

        RestTemplate restTemplate = new RestTemplate();

        pizza = restTemplate.getForEntity(String.format(apiUrl, "Dwt5F7KAhi"), Product.class).getBody();
        burger = restTemplate.getForEntity(String.format(apiUrl, "PWWe3w1SDU"), Product.class).getBody();
        salad = restTemplate.getForEntity(String.format(apiUrl, "C8GDyLrHJb"), Product.class).getBody();
        fries = restTemplate.getForEntity(String.format(apiUrl, "4MB7UfpTQs"), Product.class).getBody();

        pizza.setPrice(pizza.getPrice().multiply(DECIMAL_FACTOR));
        pizza.getPromotions().forEach(promotion -> promotion.setPrice(promotion.getPrice().multiply(DECIMAL_FACTOR)));
        burger.setPrice(burger.getPrice().multiply(DECIMAL_FACTOR));
        salad.setPrice(salad.getPrice().multiply(DECIMAL_FACTOR));
        fries.setPrice(fries.getPrice().multiply(DECIMAL_FACTOR));


    }

    @Test
    void checkoutScenario1() {
        List<Product> products = new ArrayList<>();

        checkoutService.addItem(products, pizza);
        checkoutService.addItem(products, burger);
        checkoutService.addItem(products, fries);

        CheckoutDetailDTO checkout = checkoutService.checkout(products);

        assertThat(checkout.getItems().size()).isEqualTo(3);
        assertThat(checkout.getTotalPrice()).isEqualTo(BigDecimal.valueOf(22.97));
        assertThat(checkout.getTotalSaved()).isEqualTo(BigDecimal.valueOf(0.00).setScale(2, RoundingMode.CEILING));
    }

    @Test
    void checkoutScenario2() {
        List<Product> products = new ArrayList<>();

        checkoutService.addItem(products, pizza);
        checkoutService.addItem(products, burger);
        checkoutService.addItem(products, salad);
        checkoutService.addItem(products, fries);

        CheckoutDetailDTO checkout = checkoutService.checkout(products);

        assertThat(checkout.getItems().size()).isEqualTo(4);
        assertThat(checkout.getTotalPrice()).isEqualTo(BigDecimal.valueOf(27.96));
        assertThat(checkout.getTotalSaved()).isEqualTo(BigDecimal.valueOf(0.49));
    }

    @Test
    void checkoutScenario3() {
        List<Product> products = new ArrayList<>();

        checkoutService.addItem(products, pizza);
        checkoutService.addItem(products, pizza);
        checkoutService.addItem(products, fries);

        CheckoutDetailDTO checkout = checkoutService.checkout(products);

        assertThat(checkout.getItems().size()).isEqualTo(3);
        assertThat(checkout.getTotalPrice()).isEqualTo(BigDecimal.valueOf(23.97));
        assertThat(checkout.getTotalSaved()).isEqualTo(BigDecimal.valueOf(3.99));
    }

    @Test
    void checkoutScenario4() {
        List<Product> products = new ArrayList<>();

        checkoutService.addItem(products, burger);
        checkoutService.addItem(products, burger);
        checkoutService.addItem(products, burger);
        checkoutService.addItem(products, salad);

        CheckoutDetailDTO checkout = checkoutService.checkout(products);

        assertThat(checkout.getItems().size()).isEqualTo(4);
        assertThat(checkout.getTotalPrice()).isEqualTo(BigDecimal.valueOf(34.96));
        assertThat(checkout.getTotalSaved()).isEqualTo(BigDecimal.valueOf(10.48));
    }


}