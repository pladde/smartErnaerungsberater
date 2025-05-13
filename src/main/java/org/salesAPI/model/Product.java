package org.salesAPI.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private String catergory;
    private int quantity;
    private String manufactor;
    private String importSource;
    private LocalDateTime importDate;

    // Konstruktoren, Getter und Setter

    // Standard-Konstruktor
    public Product() {}

    public Product(String productName, String catergory, int quantity, String manufactor, String importSource, LocalDateTime importDate) {
        this.productName = productName;
        this.catergory = catergory;
        this.quantity = quantity;
        this.manufactor = manufactor;
        this.importSource = importSource;
        this.importDate = importDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImportSource() {
        return importSource;
    }

    public void setImportSource(String importSource) {
        this.importSource = importSource;
    }

    public LocalDateTime getImportDate() {
        return importDate;
    }

    public void setImportDate(LocalDateTime importDate) {
        this.importDate = importDate;
    }
}
