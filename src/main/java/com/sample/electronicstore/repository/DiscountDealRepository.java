package com.sample.electronicstore.repository;

import com.sample.electronicstore.entity.DiscountDeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiscountDealRepository extends JpaRepository<DiscountDeal, Long> {
    Optional<DiscountDeal> findByProductIdAndActive(final Long productId, final boolean active);

    List<DiscountDeal> findByProductId(final Long productId);
}
