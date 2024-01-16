package com.sample.electronicstore.repository;

import com.sample.electronicstore.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
}

