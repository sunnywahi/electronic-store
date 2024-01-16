package com.sample.electronicstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class ElectronicStoreApplication {

    private static final Logger logger = LoggerFactory.getLogger(ElectronicStoreApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ElectronicStoreApplication.class, args);
        logger.info("Electronic Store Application Started Successfully");
    }
}
