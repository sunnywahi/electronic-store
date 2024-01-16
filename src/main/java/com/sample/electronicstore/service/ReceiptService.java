package com.sample.electronicstore.service;

import com.sample.electronicstore.dto.ReceiptDTO;
import com.sample.electronicstore.entity.Basket;
import com.sample.electronicstore.entity.BasketItem;
import com.sample.electronicstore.entity.DiscountDeal;
import com.sample.electronicstore.entity.Product;
import com.sample.electronicstore.entity.Receipt;
import com.sample.electronicstore.repository.BasketRepository;
import com.sample.electronicstore.repository.DiscountDealRepository;
import com.sample.electronicstore.repository.ReceiptRepository;
import com.sample.electronicstore.utils.ConvertToDtoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service class for managing receipts in an electronic store application.
 * This class provides functionality to calculate receipts for customer baskets,
 * taking into account the current discount deals applicable to the products in the basket.
 *
 * <p>It supports dynamic discount rules based on the descriptions provided in the DiscountDeal entities.</p>
 */
@Service
@Transactional
public class ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final BasketRepository basketRepository;
    private final DiscountDealRepository discountDealRepository;

    private static final Logger logger = LoggerFactory.getLogger(ReceiptService.class);

    private static final Pattern BUY_N_GET_M_FREE_PATTERN = Pattern.compile("Buy (\\d+) Get (\\d+) Free", Pattern.CASE_INSENSITIVE);
    private static final Pattern BUY_N_GET_M_PERCENT_OFF_PATTERN = Pattern.compile("Buy (\\d+) Get (\\d+)% off on the next", Pattern.CASE_INSENSITIVE);

    /**
     * Constructs a ReceiptService with necessary repositories.
     *
     * @param receiptRepository      Repository for receipt data operations.
     * @param basketRepository       Repository for basket data operations.
     * @param discountDealRepository Repository for discount deal data operations.
     */
    @Autowired
    public ReceiptService(final ReceiptRepository receiptRepository, final BasketRepository basketRepository, final DiscountDealRepository discountDealRepository) {
        this.receiptRepository = receiptRepository;
        this.basketRepository = basketRepository;
        this.discountDealRepository = discountDealRepository;
    }

    /**
     * Calculates the receipt for a given basket ID. This includes the total price,
     * with discounts applied if applicable, and details of all items and discounts.
     *
     * @param basketId The ID of the customer's basket.
     * @return A ReceiptDTO that contains the calculated total and details of the receipt.
     * @throws NoSuchElementException if the basket is not found.
     */
    public ReceiptDTO calculateReceipt(final Long basketId) {
        final Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new NoSuchElementException("Basket not found for Id {}" + basketId));

        double total = 0;
        final Set<DiscountDeal> appliedDeals = new HashSet<>();
        final StringBuilder detailsBuilder = new StringBuilder();
        final StringBuilder itemsBuilder = new StringBuilder();

        for (final BasketItem item : basket.getItems()) {
            final Product product = item.getProduct();
            int quantity = item.getQuantity();
            double price = product.getPrice() * quantity;

            itemsBuilder.append(" - Product: ").append(product.getName())
                    .append(", Quantity: ").append(item.getQuantity()).append("\n");

            final DiscountDeal deal = discountDealRepository.findByProductIdAndActive(product.getId(), true)
                    .orElse(null);

            logger.info("found discount deal {} for product id {}", deal, product.getId());
            itemsBuilder.append("Applied Discounts:\n");
            if (deal != null) {
                // Parse the deal description and apply the discount
                final DiscountAppliedResult result = applyDiscount(deal, product, quantity);
                price = result.getPriceAfterDiscount();
                if (result.isDiscountApplied()) {
                    logger.info("price calculated with discount for basketItem {} is {}", item.getId(), price);
                    itemsBuilder.append(" - ").append(deal.getDealDescription()).append("\n");
                    appliedDeals.add(deal);
                }
            } else {
                logger.info("No discount applied for product {} and after discount price is {}", product, price);
            }
            total += price;
        }

        final Receipt receipt = new Receipt();
        receipt.setBasketId(basketId);
        receipt.setDiscountDeals(appliedDeals);
        receipt.setTotal(total);
        final Receipt savedReceipt = receiptRepository.save(receipt);
        detailsBuilder.append("Receipt ID: ").append(savedReceipt.getId()).append("\n");
        detailsBuilder.append("Items:\n");
        detailsBuilder.append(itemsBuilder);
        detailsBuilder.append("Total: ").append(savedReceipt.getTotal()).append("\n");

        return ConvertToDtoUtil.convertToReceiptDTO(savedReceipt, detailsBuilder.toString());
    }

    /**
     * Explanation of the Generic Discount Logic:
     * <p>
     * 1. **Deal Description Parsing**: The `applyDiscount` method takes a `DiscountDeal` object and matches its description.
     * <p>
     * 2. **Applying the Discount**: Based on the extracted numbers and the quantity of the product in the basket, the discount is calculated.
     * <p>
     * 3. **Flexible Discount Application**: This method can be extended or modified to handle different types of discounts. The key is how the deal description is structured and pattern matched.
     * <p>
     * 4. **Handling Complex Deals**: For more complex deals we might need a more sophisticated parser or even a small rule engine.
     *
     * @param deal
     * @param product
     * @param quantity
     * @return
     */
    private DiscountAppliedResult applyDiscount(final DiscountDeal deal, final Product product, final int quantity) {
        final Matcher freeMatcher = BUY_N_GET_M_FREE_PATTERN.matcher(deal.getDealDescription());
        final Matcher percentOffMatcher = BUY_N_GET_M_PERCENT_OFF_PATTERN.matcher(deal.getDealDescription());

        if (freeMatcher.matches()) {
            int n = Integer.parseInt(freeMatcher.group(1));
            int m = Integer.parseInt(freeMatcher.group(2));
            return applyBuyNGetMFree(n, m, product, quantity);
        } else if (percentOffMatcher.matches()) {
            int n = Integer.parseInt(percentOffMatcher.group(1));
            int m = Integer.parseInt(percentOffMatcher.group(2));
            return applyBuyNGetMPercentOff(n, m, product, quantity);
        } else {
            double price = product.getPrice() * quantity;
            logger.info("No discount applied for product {} and after discount price is {}", product, price);
            return new DiscountAppliedResult(false, price);
        }

    }

    /**
     * This method doesn't support fractional as int division automatically truncates the value
     *
     * @param buyN
     * @param getM
     * @param product
     * @param quantity
     * @return
     */
    private DiscountAppliedResult applyBuyNGetMFree(int buyN, int getM, Product product, int quantity) {
        int totalSets = quantity / (buyN + getM); // Calculate total complete sets
        int freeItems = totalSets * getM; // Calculates free items based on totalSets only
        logger.info("number of free items {} is for given quantity {}", freeItems, quantity);
        double priceAfterDiscount = product.getPrice() * (quantity - freeItems);
        return new DiscountAppliedResult(true, priceAfterDiscount);
    }

    private DiscountAppliedResult applyBuyNGetMPercentOff(int buyN, int percent, Product product, int quantity) {
        if (quantity > buyN) {
            int discountableItems = (quantity - buyN) / (buyN + 1);
            double discount = product.getPrice() * discountableItems * (percent / 100.0);
            logger.info("discount items {} and follwing percent {} is applied resulting in discount of {}", discountableItems, percent, discount);
            double priceAfterDiscount = (product.getPrice() * quantity) - discount;
            logger.info("discount applied for product {} and after discount price is {}", product, priceAfterDiscount);
            return new DiscountAppliedResult(true, priceAfterDiscount);
        } else {
            return new DiscountAppliedResult(false, product.getPrice() * quantity);
        }
    }

    /**
     * Inner class representing the result of a discount application. It holds information
     * about whether a discount was applied and the price after applying the discount.
     */
    private static class DiscountAppliedResult {
        private final boolean discountApplied;
        private final double priceAfterDiscount;
        /**
         * Constructs a DiscountAppliedResult.
         *
         * @param discountApplied Indicates if a discount was applied.
         * @param priceAfterDiscount The total price after applying the discount, if any.
         */
        public DiscountAppliedResult(final boolean discountApplied, final double priceAfterDiscount) {
            this.discountApplied = discountApplied;
            this.priceAfterDiscount = priceAfterDiscount;
        }
        /**
         * Checks if a discount was applied.
         *
         * @return True if a discount was applied, otherwise false.
         */
        public boolean isDiscountApplied() {
            return discountApplied;
        }
        /**
         * Retrieves the price after discount.
         *
         * @return The price after applying the discount.
         */
        public double getPriceAfterDiscount() {
            return priceAfterDiscount;
        }
    }
}
