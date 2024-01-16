package com.sample.electronicstore.controller;

import com.sample.electronicstore.dto.DiscountDealDTO;
import com.sample.electronicstore.dto.ProductDTO;
import com.sample.electronicstore.service.DiscountDealService;
import com.sample.electronicstore.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @Autowired
    private DiscountDealService discountDealService;


    @Test
    public void testCreateProduct() throws Exception {
        String productJson = "{\"name\":\"Test Product\", \"description\":\"A test product\", \"price\":100.0}";

        mockMvc.perform(post("/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value
                        ("Test Product"))
                .andExpect(jsonPath("$.description").value("A test product"))
                .andExpect(jsonPath("$.price").value(100.0));
    }

    @Test
    public void testRemoveProduct() throws Exception {
        //Given setup a product with id 1
        final ProductDTO productDTO = productService.saveProduct(new ProductDTO(null, "Soft Drink", "Cold Drinks", 6.0, 0));
        // Assuming product with ID 1 exists in the database
        mockMvc.perform(delete("/admin/products/"+productDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Product removed successfully"));
    }

    @Test
    public void testAddDiscountDeal() throws Exception {
        //Given setup a product with id 1
        final ProductDTO productDTO = productService.saveProduct(new ProductDTO(null, "Random Drink", "Random Drinks", 6.0, 0));

        String dealJson = "{\"productId\":" + productDTO.getId()+ ", \"dealDescription\":\"Buy 1 Get 1 Free\"}";

        mockMvc.perform(post("/admin/discount-deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dealJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(productDTO.getId()))
                .andExpect(jsonPath("$.dealDescription").value("BUY 1 GET 1 FREE"));
    }

    @Test
    public void testAllProducts() throws Exception {
        mockMvc.perform(post("/admin/all-products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0)))); // Assuming the list can be empty
    }

    @Test
    public void testGetAllDiscountDealsForProductId() throws Exception {
        // Assuming product with ID 1 exists
        mockMvc.perform(get("/admin/discount-deals/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0)))); // List size check
    }

    @Test
    public void testGetActiveDiscountDeal() throws Exception {
        final ProductDTO productDTO = productService.saveProduct(new ProductDTO(null, "Discount Drink", "Random Drinks", 6.0, 0));
        final DiscountDealDTO discountDealDTO = discountDealService.saveDiscountDeal(new DiscountDealDTO(null, productDTO.getId(), "Buy 1 Get 2 Free", true, 0));
        // Assuming product with ID 1 exists and has an active deal
        mockMvc.perform(get("/admin/active-discount-deals/"+productDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(productDTO.getId())); // Verifying returned deal
    }
    @Test
    public void testAllDiscountDeals() throws Exception {
        mockMvc.perform(post("/admin/all-discount-deals"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0)))); // Assuming the list can be empty
    }

}

