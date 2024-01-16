package com.sample.electronicstore.controller;

import com.sample.electronicstore.dto.BasketDTO;
import com.sample.electronicstore.dto.ReceiptDTO;
import com.sample.electronicstore.service.BasketService;
import com.sample.electronicstore.service.ReceiptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * REST controller for customer-related operations in an electronic store application.
 * Provides endpoints for managing customer baskets and calculating receipts.
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final BasketService basketService;
    private final ReceiptService receiptService;

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    /**
     * Constructs a CustomerController with necessary services for basket and receipt operations.
     *
     * @param basketService Service for basket-related operations.
     * @param receiptService Service for receipt calculation operations.
     */
    @Autowired
    public CustomerController(final BasketService basketService, final ReceiptService receiptService) {
        this.basketService = basketService;
        this.receiptService = receiptService;
    }

    /**
     * Adds a product to a customer's basket. Creates a new basket if one doesn't already exist.
     *
     * @param customerId The ID of the customer.
     * @param productId The ID of the product to add.
     * @param quantity The quantity of the product to add.
     * @return ResponseEntity containing the updated BasketDTO.
     */
    @PostMapping("/basket")
    public ResponseEntity<?> addToBasket(@RequestParam final Long customerId,
                                                 @RequestParam final Long productId,
                                                 @RequestParam final int quantity) {
        try {
            logger.info("adding productId {} with Qty {} to customer's {} basket", productId, quantity, customerId);
            final BasketDTO basketDTO = basketService.addToBasket(customerId, productId, quantity);
            return new ResponseEntity(basketDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Removes an item from a customer's basket.
     *
     * @param basketItemId The ID of the basket item to remove.
     * @return ResponseEntity with a success message.
     */
    @DeleteMapping("/basket/{basketItemId}")
    public ResponseEntity<String> removeFromBasket(@PathVariable final Long basketItemId) {
        try{
            basketService.removeFromBasket(basketItemId);
            logger.info("successfully deleted the basketItem with id{}", basketItemId);
            return new ResponseEntity<>("BasketItem removed successfully", HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Calculates and returns a receipt for a given basket.
     *
     * @param basketId The ID of the basket for which to calculate the receipt.
     * @return ResponseEntity containing the calculated ReceiptDTO.
     */
    @GetMapping("/receipt/{basketId}")
    public ResponseEntity<ReceiptDTO> calculateReceipt(@PathVariable final Long basketId) {
        final ReceiptDTO receiptDTO = receiptService.calculateReceipt(basketId);
        return ResponseEntity.ok(receiptDTO);
    }
}

