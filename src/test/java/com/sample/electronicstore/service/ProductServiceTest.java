package com.sample.electronicstore.service;

import com.sample.electronicstore.dto.ProductDTO;
import com.sample.electronicstore.entity.Product;
import com.sample.electronicstore.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        product = new Product(1L, "Crypto", "High-end crypto", 1500.00, Instant.parse("2024-01-15T18:35:24.00Z"));
        productDTO = new ProductDTO(1L, "Crypto", "High-end crypto", 1500.00, 0L);
    }

    @Test
    public void testSaveProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        final ProductDTO savedProductDTO = productService.saveProduct(productDTO);

        assertNotNull(savedProductDTO);
        assertEquals(productDTO.getName(), savedProductDTO.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testGetProductById() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        final ProductDTO foundProductDTO = productService.getProductById(1L);

        assertNotNull(foundProductDTO);
        assertEquals(product.getName(), foundProductDTO.getName());
        verify(productRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));

        final List<ProductDTO> products = productService.getAllProducts();

        assertNotNull(products);
        assertFalse(products.isEmpty());
        assertEquals(1, products.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    public void testRemoveProduct() {
        doNothing().when(productRepository).deleteById(anyLong());

        productService.removeProduct(1L);

        verify(productRepository, times(1)).deleteById(anyLong());
    }

}
