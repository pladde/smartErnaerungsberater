package org.salesAPI.service;

import org.salesAPI.model.Product;
import org.salesAPI.repository.ProductRepository;
import org.salesAPI.util.XmlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import org.salesAPI.util.CsvParser;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductService {

    @Autowired
            private ProductRepository productRepository;

    @Autowired
            private CsvParser csvParser;

    @Autowired
            private XmlParser xmlParser;

    List<Product> testListe = new ArrayList<>();

    // CSV-Import-Daten
    public void importCSV(MultipartFile file) {
        testListe = csvParser.parseCsvToProducts(file);
        productRepository.saveAll(testListe);
    }

    // XML Import Daten
    public void importXml(MultipartFile file) {
        testListe = xmlParser.parseXmlToProducts(file);
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