package com.sample.electronicstore.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

/**
 * Discount deal can house discounts with many products, but at any point only one discount will be active for one product.
 */

@Entity
@Table(name = "discount_deal")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDeal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //id of the product that is having some discount setup on it
    @Column(name = "product_id")
    private Long productId;
    //discount on format where part 1 is qty and part 4 is discount
    @Column(name = "deal_description")
    private String dealDescription;
    //There can only be one active discount deal for a product
    private boolean active;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private Instant lastUpdated;
}
