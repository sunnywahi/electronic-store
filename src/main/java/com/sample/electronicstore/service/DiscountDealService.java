package com.sample.electronicstore.service;

import com.sample.electronicstore.dto.DiscountDealDTO;
import com.sample.electronicstore.entity.DiscountDeal;
import com.sample.electronicstore.exception.StoreOperationException;
import com.sample.electronicstore.repository.DiscountDealRepository;
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
 * Service class for managing discount deals in an electronic store application.
 * This class provides functionalities for creating, updating, retrieving, and deleting discount deals.
 *
 * <p>Uses pessimistic locking to ensure safe concurrent operations, particularly important
 * for operations that affect the availability and terms of discount deals.</p>
 *
 * <p>The {@link Transactional} annotation ensures that operations are executed within a transaction context,
 * providing atomicity and enabling automatic rollback on runtime exceptions.</p>
 */
@Service
@Transactional
public class DiscountDealService {
    private final DiscountDealRepository discountDealRepository;

    private static final Logger logger = LoggerFactory.getLogger(DiscountDealService.class);

    //using finer grained locking here
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Constructs a DiscountDealService with the required DiscountDealRepository.
     *
     * @param discountDealRepository Repository for discount deal data operations.
     */
    @Autowired
    public DiscountDealService(final DiscountDealRepository discountDealRepository) {
        this.discountDealRepository = discountDealRepository;
    }

    /**
     * Creates or updates a discount deal. If an active deal exists for the same product,
     * it is deactivated before saving the new deal.
     *
     * @param discountDealDTO Data Transfer Object containing discount deal details.
     * @return The saved DiscountDealDTO with updated information.
     * @throws StoreOperationException if the discount deal cannot be saved.
     */
    public DiscountDealDTO saveDiscountDeal(final DiscountDealDTO discountDealDTO) {
        lock.lock();
        try{
            // Check if there's an existing active deal for the product
            final Optional<DiscountDeal> existingDeal = discountDealRepository
                    .findByProductIdAndActive(discountDealDTO.getProductId(), true);

            if (existingDeal.isPresent()) {
                logger.info("found an active deal {} thus deactivating it", existingDeal);
                final DiscountDeal currentActiveDeal = existingDeal.get();
                if(currentActiveDeal.getDealDescription().equalsIgnoreCase(discountDealDTO.getDealDescription())){
                    currentActiveDeal.setActive(discountDealDTO.isActive());
                    return ConvertToDtoUtil.convertToDiscountDealDTO(discountDealRepository.save(currentActiveDeal));
                }
                else{
                    currentActiveDeal.setActive(false);
                    discountDealRepository.save(currentActiveDeal);
                }
            }
            final DiscountDeal discountDeal = ConvertToEntityUtil.convertToDiscountDeal(discountDealDTO);
            discountDeal.setActive(true);
            discountDeal.setDealDescription(discountDeal.getDealDescription().toUpperCase());
            final DiscountDeal savedDiscountDeal = discountDealRepository.save(discountDeal);
            logger.info("saved discount deal {}", savedDiscountDeal);
            return ConvertToDtoUtil.convertToDiscountDealDTO(savedDiscountDeal);
        }catch (Exception e){
            throw new StoreOperationException("Unable to save discount deal", e);
        }finally {
            lock.unlock();
        }
    }

    /**
     * Retrieves a discount deal by its ID.
     *
     * @param id The unique identifier of the discount deal.
     * @return DiscountDealDTO representing the discount deal.
     * @throws StoreOperationException if the discount deal is not found.
     */
    public DiscountDealDTO getDiscountDealById(final Long id) {
        final DiscountDeal discountDeal = discountDealRepository.findById(id)
                .orElseThrow(() -> new StoreOperationException("Discount Deal not found"));
        return ConvertToDtoUtil.convertToDiscountDealDTO(discountDeal);
    }

    /**
     * Retrieves all discount deals available in the store.
     *
     * @return A list of DiscountDealDTOs representing all discount deals.
     */
    public List<DiscountDealDTO> getAllDiscountDeals() {
        List<DiscountDealDTO> discountDealDTOS = new ArrayList<>();
        for (final DiscountDeal discountDeal : discountDealRepository.findAll()) {
            DiscountDealDTO discountDealDTO = ConvertToDtoUtil.convertToDiscountDealDTO(discountDeal);
            discountDealDTOS.add(discountDealDTO);
        }
        return discountDealDTOS;
    }

    /**
     * Removes a discount deal from the store by its ID.
     * Uses fine-grained locking to ensure thread safety during the operation.
     *
     * @param id The unique identifier of the discount deal to be removed.
     * @throws StoreOperationException if the discount deal cannot be removed.
     */
    public void removeDiscountDeal(final Long id) {
        lock.lock();
        try{
            discountDealRepository.deleteById(id);
        }catch (Exception e){
            throw new StoreOperationException("unable to delete discount deal", e);
        }finally {
            lock.unlock();
        }
    }

    /**
     * Retrieves all discount deals for a specific product ID.
     *
     * @param productId The ID of the product for which to find discount deals.
     * @return A list of DiscountDealDTOs for the specified product.
     */
    public List<DiscountDealDTO> getDiscountDealsForProductId(final Long productId) {
        final List<DiscountDealDTO> discountDealDTOS = new ArrayList<>();
        for (final DiscountDeal discountDeal : discountDealRepository.findByProductId(productId)) {
            DiscountDealDTO discountDealDTO = ConvertToDtoUtil.convertToDiscountDealDTO(discountDeal);
            discountDealDTOS.add(discountDealDTO);
        }
        return discountDealDTOS;
    }

    /**
     * Retrieves the active discount deal for a specific product ID, if available.
     *
     * @param productId The ID of the product for which to find the active discount deal.
     * @return DiscountDealDTO representing the active discount deal, if present.
     */
    public DiscountDealDTO getActiveDiscountDealForProductId(final Long productId){
        final Optional<DiscountDeal> byProductIdAndActive = discountDealRepository.findByProductIdAndActive(productId, true);
        return byProductIdAndActive.map(ConvertToDtoUtil::convertToDiscountDealDTO).orElseGet(DiscountDealDTO::new);
    }
}


