package com.sample.electronicstore.utils;

import com.sample.electronicstore.dto.BasketDTO;
import com.sample.electronicstore.dto.BasketItemDTO;
import com.sample.electronicstore.dto.DiscountDealDTO;
import com.sample.electronicstore.dto.ProductDTO;
import com.sample.electronicstore.dto.ReceiptDTO;
import com.sample.electronicstore.entity.Basket;
import com.sample.electronicstore.entity.BasketItem;
import com.sample.electronicstore.entity.DiscountDeal;
import com.sample.electronicstore.entity.Product;
import com.sample.electronicstore.entity.Receipt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConvertToDtoUtil {
    private ConvertToDtoUtil(){}

    public static ProductDTO convertToProductDTO(final Product product) {
        return new ProductDTO(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getLastUpdated() != null ? product.getLastUpdated().toEpochMilli(): 0);
    }

    public static DiscountDealDTO convertToDiscountDealDTO(final DiscountDeal discountDeal){
        return new DiscountDealDTO(discountDeal.getId(), discountDeal.getProductId(), discountDeal.getDealDescription(), discountDeal.isActive(), discountDeal.getLastUpdated() != null ? discountDeal.getLastUpdated().toEpochMilli(): 0);
    }

    public static BasketDTO convertToBasketDTO(final Basket basket){
        final List<BasketItemDTO> itemDTOs = new ArrayList<>();
        for (BasketItem item : basket.getItems()) {
            BasketItemDTO basketItemDTO = new BasketItemDTO(item.getId(), basket.getId(), item.getProduct().getId(), item.getQuantity(), item.getLastUpdated() !=null ? item.getLastUpdated().toEpochMilli(): 0);
            itemDTOs.add(basketItemDTO);
        }
        return new BasketDTO(basket.getId(), basket.getCustomerId(), itemDTOs, basket.getLastUpdated() != null ? basket.getLastUpdated().toEpochMilli() : 0);
    }

    public static ReceiptDTO convertToReceiptDTO(final Receipt receipt, final String details) {
        final Set<Long> discountDealIds = receipt.getDiscountDeals().stream()
                .map(DiscountDeal::getId)
                .collect(Collectors.toSet());
        return new ReceiptDTO(receipt.getId(), receipt.getBasketId(), discountDealIds, details, receipt.getLastUpdated() !=null ? receipt.getLastUpdated().toEpochMilli() : 0);
    }
}
