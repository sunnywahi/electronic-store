package com.sample.electronicstore.repository;

import com.sample.electronicstore.entity.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BasketRepository extends JpaRepository<Basket, Long> {

    Optional<Basket> findByCustomerId(final Long customerId);
}

