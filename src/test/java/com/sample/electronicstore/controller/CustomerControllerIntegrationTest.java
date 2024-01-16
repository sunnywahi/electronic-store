package com.sample.electronicstore.controller;

import com.sample.electronicstore.entity.Basket;
import com.sample.electronicstore.entity.BasketItem;
import com.sample.electronicstore.entity.Product;
import com.sample.electronicstore.repository.BasketItemRepository;
import com.sample.electronicstore.repository.BasketRepository;
import com.sample.electronicstore.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private BasketItemRepository basketItemRepository;

    private Product savedProduct;
    private Basket savedBasket;

    @BeforeEach
    public void setUp(){
        savedProduct = productRepository.save(new Product(null, "Soft Drink", "Cold Drinks", 6.0, null));

        final Basket basket = new Basket(999L);
        final BasketItem basketItem = new BasketItem();
        basketItem.setBasket(basket);
        basketItem.setProduct(savedProduct);
        basketItem.setQuantity(3);

        if(basket.getItems() == null){
            basket.setItems(new ArrayList<>());
        }
        basket.getItems().add(basketItem);
        savedBasket = basketRepository.save(basket);
    }

    @AfterEach
    public void deleteData(){
        basketRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    public void testAddToBasket() throws Exception {
        // Assuming customerId 1 and productId 1 exist in the database
        mockMvc.perform(post("/customer/basket")
                        .param("customerId", "999")
                        .param("productId", ""+savedProduct.getId())
                        .param("quantity", "2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(999));

    }


    @Test
    public void testRemoveFromBasket() throws Exception {
        // Assuming basketItemId 1 exists in the basket
        mockMvc.perform(delete("/customer/basket/"+savedBasket.getItems().get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("BasketItem removed successfully"));

    }

    @Test
    public void testCalculateReceipt() throws Exception {
        // Assuming basketId 1 exists and has items
        mockMvc.perform(get("/customer/receipt/"+ savedBasket.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basketId").value(savedBasket.getId()));
    }

}

