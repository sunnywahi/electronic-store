package com.sample.electronicstore.service;

import com.sample.electronicstore.dto.BasketDTO;
import com.sample.electronicstore.entity.Basket;
import com.sample.electronicstore.entity.Product;
import com.sample.electronicstore.repository.BasketItemRepository;
import com.sample.electronicstore.repository.BasketRepository;
import com.sample.electronicstore.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BasketServiceTest {

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BasketItemRepository basketItemRepository;

    @InjectMocks
    private BasketService basketService;

    private Product product;
    private Basket basket;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        product = new Product(1L, "Crypto", "High-end crypto", 1500.00, Instant.parse("2024-01-15T18:35:24.00Z"));
        basket = new Basket(1L);
        basket.setItems(new ArrayList<>());
    }

    @Test
    public void testAddToBasket() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(basketRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(basket));
        when(basketRepository.save(any(Basket.class))).thenReturn(basket);

        final BasketDTO basketDTO = basketService.addToBasket(1L, 1L, 2);

        assertNotNull(basketDTO);
        assertFalse(basketDTO.getItems().isEmpty());
        verify(basketRepository, times(1)).findByCustomerId(anyLong());
        verify(basketRepository, times(1)).save(any(Basket.class));
    }

    @Test
    public void testRemoveFromBasket() {
        when(basketItemRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(basketItemRepository).deleteById(anyLong());

        basketService.removeFromBasket(1L);

        verify(basketItemRepository, times(1)).deleteById(anyLong());
    }
}
