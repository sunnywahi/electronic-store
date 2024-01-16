package com.sample.electronicstore.service;

import com.sample.electronicstore.dto.ProductDTO;
import com.sample.electronicstore.entity.Product;
import com.sample.electronicstore.exception.StoreOperationException;
import com.sample.electronicstore.repository.ProductRepository;
import com.sample.electronicstore.utils.ConvertToDtoUtil;
import com.sample.electronicstore.utils.ConvertToEntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service class for managing products in an electronic store application.
 * This class provides functionalities for creating, updating, retrieving, and deleting products.
 *
 * <p>Uses pessimistic locking (finer grained locks) to ensure safe concurrent operations on products,
 * making it suitable for scenarios where product updates are less frequent but require high consistency.
 * This approach is efficient for a single instance application but may not be suitable for distributed environments.</p>
 *
 * <p>The {@link Transactional} annotation ensures that operations are executed within a transaction context,
 * providing atomicity and enabling automatic rollback on runtime exceptions.</p>
 */
@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    //using finer grained locking here
    private final ReentrantLock lock = new ReentrantLock();
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    /**
     * Constructs a ProductService with the required ProductRepository.
     *
     * @param productRepository Repository for product data operations.
     */
    @Autowired
    public ProductService(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    /**
     * Creates or updates a product in the store.
     * Uses fine-grained locking to ensure thread safety during the operation.
     *
     * @param productDTO Data Transfer Object containing product details.
     * @return The saved ProductDTO with updated information.
     * @throws StoreOperationException if the product cannot be saved.
     */
    public ProductDTO saveProduct(final ProductDTO productDTO) {
        //taking fine grain locking
        lock.lock();
        try {
            final Product product = ConvertToEntityUtil.convertToProduct(productDTO);
            final Product savedProduct = productRepository.save(product);
            logger.info("new product saved {}", product);
            return ConvertToDtoUtil.convertToProductDTO(savedProduct);
        } catch(Exception e){
            throw new StoreOperationException("Failed to save product", e);
        }finally {
            lock.unlock();
        }
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id The unique identifier of the product.
     * @return ProductDTO representing the product.
     * @throws StoreOperationException if the product is not found.
     */
    public ProductDTO getProductById(final Long id) {
        final Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()){
            return ConvertToDtoUtil.convertToProductDTO(product.get());
        }else{
            logger.error("product not found for id {}", id);
            throw new StoreOperationException("Product Not Found");
        }
    }

    /**
     * Retrieves all products available in the store.
     *
     * @return A list of ProductDTOs representing all products.
     */
    public List<ProductDTO> getAllProducts() {
        final List<ProductDTO> productList = new ArrayList<>();
        for (Product product : productRepository.findAll()) {
            ProductDTO productDTO = ConvertToDtoUtil.convertToProductDTO(product);
            productList.add(productDTO);
        }
        return productList;
    }

    /**
     * Removes a product from the store by its ID.
     * Uses fine-grained locking to ensure thread safety during the operation.
     *
     * @param id The unique identifier of the product to be removed.
     * @throws StoreOperationException if the product cannot be removed.
     */
    public void removeProduct(final Long id) {
        lock.lock();
        try {
            productRepository.deleteById(id);
        } catch(Exception e){
            throw new StoreOperationException("Failed to remove product", e);
        }finally {
            lock.unlock();
        }
    }
}


