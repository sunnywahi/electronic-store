package com.sample.electronicstore.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * This entity class uses JPA and also, we are using lombok here.
 */
@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //name of the Product
    @Column(unique = true, nullable = false)
    private String name;
    //description, about the product
    private String description;
    //price of the product
    private double price;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private Instant lastUpdated;
}
