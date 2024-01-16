package com.sample.electronicstore.service;

import com.sample.electronicstore.dto.ReceiptDTO;
import com.sample.electronicstore.entity.Basket;
import com.sample.electronicstore.entity.BasketItem;
import com.sample.electronicstore.entity.DiscountDeal;
import com.sample.electronicstore.entity.Product;
import com.sample.electronicstore.entity.Receipt;
import com.sample.electronicstore.repository.BasketRepository;
import com.sample.electronicstore.repository.DiscountDealRepository;
import com.sample.electronicstore.repository.ProductRepository;
import com.sample.electronicstore.repository.ReceiptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.*;

public class ReceiptServiceTest {

    @Mock
    private ReceiptRepository receiptRepository;

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private DiscountDealRepository discountDealRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ReceiptService receiptService;

    private Basket basket;
    private Product product;
    private DiscountDeal discountDeal;

    private BasketItem basketItem;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        product = new Product(1L, "Crypto", "AVAX", 1500.00, Instant.parse("2024-01-15T18:35:24.00Z"));
        discountDeal = new DiscountDeal(1L, 1L, "Buy 1 Get 1 Free", true, Instant.parse("2024-01-15T18:35:24.00Z"));
        basket = new Basket(1L);
        basketItem = new BasketItem(1L, basket, product, 2, Instant.parse("2024-01-15T18:35:24.00Z"));
        basket.setItems(Arrays.asList(basketItem));
    }

    @Test
    public void testCalculateReceipt() {
        when(basketRepository.findById(anyLong())).thenReturn(Optional.of(basket));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(discountDealRepository.findByProductIdAndActive(anyLong(), anyBoolean())).thenReturn(Optional.of(discountDeal));
        final Set<DiscountDeal> discountDeals = new HashSet<>();
        discountDeals.add(discountDeal);
        when(receiptRepository.save(any(Receipt.class))).thenReturn(new Receipt(1L, 1L, discountDeals, 1, Instant.parse("2024-01-15T18:35:24.00Z")));

        final ReceiptDTO receiptDTO = receiptService.calculateReceipt(1L);

        assertNotNull(receiptDTO);
        assertTrue(receiptDTO.getDiscountDealIds().contains(discountDeal.getId()));
        assertNotNull(receiptDTO.getDetails());
    }
}
