package com.homechallenge.checkout.service;

import com.homechallenge.checkout.dto.CheckoutDetailDTO;
import com.homechallenge.checkout.model.Product;

import java.util.List;

public interface CheckoutService {

    CheckoutDetailDTO checkout(List<Product> products);

    void addItem(List<Product> products, Product product);

}
