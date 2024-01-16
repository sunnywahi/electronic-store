package com.sample.electronicstore.service;

import com.sample.electronicstore.dto.BasketDTO;
import com.sample.electronicstore.entity.Basket;
import com.sample.electronicstore.entity.BasketItem;
import com.sample.electronicstore.entity.Product;
import com.sample.electronicstore.exception.StoreOperationException;
import com.sample.electronicstore.repository.BasketItemRepository;
import com.sample.electronicstore.repository.BasketRepository;
import com.sample.electronicstore.repository.ProductRepository;
import com.sample.electronicstore.utils.ConvertToDtoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * Service class for managing customer baskets in an electronic store application.
 * This class provides functionalities to add items to a customer's basket,
 * remove items from the basket, and can be extended to handle other basket-related operations.
 *
 * <p>This service uses optimistic locking to ensure data consistency and to handle
 * concurrent operations on baskets, making it suitable for high-concurrency environments.</p>
 */
@Service
@Transactional
public class BasketService {
    private final BasketRepository basketRepository;
    private final ProductRepository productRepository;
    private final BasketItemRepository basketItemRepository;

    private static final Logger logger = LoggerFactory.getLogger(BasketService.class);

    /**
     * Constructs a BasketService with required repositories.
     *
     * @param basketRepository Repository for basket data operations.
     * @param productRepository Repository for product data operations.
     * @param basketItemRepository Repository for basket item data operations.
     */
    @Autowired
    public BasketService(final BasketRepository basketRepository, final ProductRepository productRepository, final BasketItemRepository basketItemRepository) {
        this.basketRepository = basketRepository;
        this.productRepository = productRepository;
        this.basketItemRepository = basketItemRepository;
    }

    /**
     * Adds a product to a customer's basket. If the basket does not exist, a new one is created.
     *
     * <p>This method is subject to a high number of operations from various customers and involves
     * multiple database operations. Optimistic locking is used to ensure consistency and handle concurrency.</p>
     * @param customerId The ID of the customer.
     * @param productId The ID of the product to add to the basket.
     * @param quantity The quantity of the product to add.
     * @return BasketDTO representing the updated basket.
     * @throws StoreOperationException if the product is not found or the basket cannot be updated.
     */
    public BasketDTO addToBasket(final Long customerId, final Long productId, int quantity) {
        final Product product = productRepository.findById(productId)
                .orElseThrow(() -> new StoreOperationException("Product not found for" +productId));

        final Basket basket = basketRepository.findByCustomerId(customerId)
                .orElse(new Basket(customerId));

        final BasketItem basketItem = new BasketItem();
        basketItem.setBasket(basket);
        basketItem.setProduct(product);
        basketItem.setQuantity(quantity);
        if(basket.getItems() == null){
            basket.setItems(new ArrayList<>());
        }
        basket.getItems().add(basketItem);
        try{
            final Basket updatedBasket = basketRepository.save(basket);
            logger.info("added basketItem with Id {} to basketId {} for customerId {}", basketItem.getId(), updatedBasket.getId(), customerId);
            return ConvertToDtoUtil.convertToBasketDTO(updatedBasket);
        }catch (Exception e){
            throw new StoreOperationException("Unable to save product to basket", e);
        }
    }

    /**
     * Removes an item from the basket.
     * @param basketItemId The ID of the basket item to remove.
     * @throws StoreOperationException if the basket item cannot be removed.
     */
    public void removeFromBasket(final Long basketItemId) {
        try {
            basketItemRepository.deleteById(basketItemId);
        }catch (Exception e){
            throw new StoreOperationException("Unable to remove basketItem", e);
        }
    }
}

