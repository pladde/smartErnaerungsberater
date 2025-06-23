package org.salesAPI.service;

import org.salesAPI.model.Product;
import org.salesAPI.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import org.salesAPI.util.CsvParser;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    List<Product> testListe = new ArrayList<>();


    // CSV-Import-Daten
    public void importCsvData(MultipartFile file) {

        // falls keine Tabelle erstellt wurde, muss eine erstellt werden

        Product p = new Product("Testprodukt", "Testkategorie", 5, "Testhersteller", "manual", LocalDateTime.now());
        productRepository.save(p);

        CsvParser csvParser = new CsvParser();
        testListe = csvParser.parseCsvToProducts(file);
        productRepository.saveAll(testListe);
    }

    // CREATE: Produkt speichern
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // READ: Alle Produkte abrufen
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // READ: Produkt anhand der ID abrufen
    public Product getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElse(null);  // Falls nicht gefunden, null zurückgeben
    }

    // UPDATE: Produkt aktualisieren
    public Product updateProduct(Long id, Product updatedProduct) {
        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            product.setProductName(updatedProduct.getProductName());
            product.setQuantity(updatedProduct.getQuantity());
            product.setImportSource(updatedProduct.getImportSource());
            product.setImportDate(updatedProduct.getImportDate());
            return productRepository.save(product);
        }
        return null;
    }

    // DELETE: Produkt löschen
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}