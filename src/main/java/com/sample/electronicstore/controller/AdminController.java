package com.sample.electronicstore.controller;

import com.sample.electronicstore.dto.DiscountDealDTO;
import com.sample.electronicstore.dto.ProductDTO;
import com.sample.electronicstore.exception.StoreOperationException;
import com.sample.electronicstore.service.DiscountDealService;
import com.sample.electronicstore.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for administrative operations in an electronic store application.
 * Provides endpoints for managing products and discount deals.
 *
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final DiscountDealService discountDealService;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    /**
     * Constructs an AdminController with necessary services.
     *
     * @param productService Service for product-related operations.
     * @param discountDealService Service for discount deal-related operations.
     */
    @Autowired
    public AdminController(final ProductService productService, final DiscountDealService discountDealService) {
        this.productService = productService;
        this.discountDealService = discountDealService;
    }

    /**
     * Creates or updates a product in the store.
     *
     * @param productDTO Data Transfer Object containing product details.
     * @return ResponseEntity with the created or updated ProductDTO and HTTP status.
     */
    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@RequestBody final ProductDTO productDTO) {
        logger.info("received request to create product {}", productDTO);
        try{
            final ProductDTO createdProduct = productService.saveProduct(productDTO);
            logger.info("created product {}", createdProduct);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        }catch (StoreOperationException exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    /**
     * Removes a product from the store by its ID.
     *
     * @param productId The ID of the product to remove.
     * @return ResponseEntity with a success message and HTTP status.
     */
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<String> removeProduct(@PathVariable final Long productId) {
        try {
            logger.info("received request to remove product with id {}", productId);
            productService.removeProduct(productId);
            return new ResponseEntity<>("Product removed successfully", HttpStatus.OK);
        } catch (StoreOperationException ex) {
            logger.error("product with id {} is not found", productId);
            // Assuming RuntimeException is thrown when product is not found
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    /**
     * Adds a new discount deal for a product.
     *
     * @param discountDealDTO Data Transfer Object containing discount deal details.
     * @return ResponseEntity with the created DiscountDealDTO and HTTP status.
     */
    @PostMapping("/discount-deals")
    public ResponseEntity<?> addDiscountDeal(@RequestBody final DiscountDealDTO discountDealDTO) {
        try {
            final DiscountDealDTO createdDeal = discountDealService.saveDiscountDeal(discountDealDTO);
            return new ResponseEntity<>(createdDeal, HttpStatus.CREATED);
        }catch (StoreOperationException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Retrieves all products available in the store.
     *
     * @return ResponseEntity with a list of all ProductDTOs and HTTP status.
     */
    @PostMapping("/all-products")
    public ResponseEntity<List<ProductDTO>> allProducts(){
        logger.info("received request to retrieve all products");
        final List<ProductDTO> allProducts = productService.getAllProducts();
        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }

    /**
     * Retrieves all discount deals for a specific product ID.
     *
     * @param productId The ID of the product for which to find discount deals.
     * @return ResponseEntity with a list of DiscountDealDTOs and HTTP status.
     */
    @GetMapping("/discount-deals/{productId}")
    public ResponseEntity<List<DiscountDealDTO>> getAllDiscountDealsForProductId(@PathVariable final Long productId){
        logger.info("received request to fetch discount deal for productId {}", productId);
        final List<DiscountDealDTO> discountDealsForProductId = discountDealService.getDiscountDealsForProductId(productId);
        return new ResponseEntity<>(discountDealsForProductId, HttpStatus.OK);
    }

    /**
     * Retrieves the active discount deal for a specific product ID, if available.
     *
     * @param productId The ID of the product for which to find the active discount deal.
     * @return ResponseEntity with the active DiscountDealDTO and HTTP status.
     */
    @GetMapping("/active-discount-deals/{productId}")
    public ResponseEntity<DiscountDealDTO> getActiveDiscountDeal(@PathVariable final Long productId){
        logger.info("received request to fetch active discount deal for productId {}", productId);
        final DiscountDealDTO discountDealsForProductId = discountDealService.getActiveDiscountDealForProductId(productId);
        return new ResponseEntity<>(discountDealsForProductId, HttpStatus.OK);
    }

    /**
     * Retrieves all discount deals available in the store.
     *
     * @return ResponseEntity with a list of all DiscountDealDTOs and HTTP status.
     */
    @PostMapping("/all-discount-deals")
    public ResponseEntity<List<DiscountDealDTO>> allDiscountDeals(){
        logger.info("received request to retrieve all discount deals");
        final List<DiscountDealDTO> discountDeals = discountDealService.getAllDiscountDeals();
        return new ResponseEntity<>(discountDeals, HttpStatus.OK);
    }

}
