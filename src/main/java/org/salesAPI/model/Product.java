package org.salesAPI.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private String category;
    private int quantity;
    private String manufacturer;
    private String importSource;
    private LocalDateTime importDate;


    // Standard-constructor
    public Product() {}

    public Product(String productName, String category, int quantity, String manufacturer, String importSource, LocalDateTime importDate) {
        this.productName = productName;
        this.category = category;
        this.quantity = quantity;
        this.manufacturer = manufacturer;
        this.importSource = importSource;
        this.importDate = importDate;
    }
}
