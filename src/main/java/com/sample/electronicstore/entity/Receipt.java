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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "receipt")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "basket_id")
    private Long basketId;
    //This is receipt_discount_deal, manages relationship of receipt to any discount_deal.
    @ManyToMany
    @JoinTable(
            name = "receipt_discount_deal", joinColumns = @JoinColumn(name = "receipt_id"), inverseJoinColumns = @JoinColumn(name = "discount_deal_id"))
    private Set<DiscountDeal> discountDeals;
    private double total;
    @UpdateTimestamp
    @Column(name = "last_updated")
    private Instant lastUpdated;
}

