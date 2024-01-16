package com.sample.electronicstore.utils;

import com.sample.electronicstore.dto.DiscountDealDTO;
import com.sample.electronicstore.dto.ProductDTO;
import com.sample.electronicstore.entity.DiscountDeal;
import com.sample.electronicstore.entity.Product;

public class ConvertToEntityUtil {

    private ConvertToEntityUtil(){
    }

    public static Product convertToProduct(ProductDTO productDTO) {
        // For new products, the ID will be null and should not be set manually
        if (productDTO.getId() == null) {
            final Product product = new Product();
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            return product;
        } else {
            // For updates, use the provided ID
            final Product product = new Product();
            product.setId(productDTO.getId());
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            return product;
        }
    }

    public static DiscountDeal convertToDiscountDeal(final DiscountDealDTO discountDealDTO) {
        if(discountDealDTO.getId() == null) {
            final DiscountDeal discountDeal = new DiscountDeal();
            discountDeal.setProductId(discountDealDTO.getProductId());
            discountDeal.setDealDescription(discountDealDTO.getDealDescription());
            discountDeal.setActive(discountDealDTO.isActive());
            return discountDeal;
        }else{
            final DiscountDeal discountDeal = new DiscountDeal();
            discountDeal.setId(discountDealDTO.getId());
            discountDeal.setProductId(discountDealDTO.getProductId());
            discountDeal.setDealDescription(discountDealDTO.getDealDescription());
            discountDeal.setActive(discountDealDTO.isActive());
            return discountDeal;
        }
    }
}
