package org.salesAPI.service;

import org.salesAPI.model.Product;
import org.salesAPI.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /*@Autowired
    private WeatherDataRepository weatherDataRepository;*/

    // CSV-Import-Daten
    public void importCsvData(String filePath) {
        // CSV-Parsing-Logik hier implementieren (z.B. CsvParser verwenden)
        // Zum Beispiel, nach dem Parsen:
        Product product = new Product("Cola", 100, "CSV", LocalDateTime.now());
        productRepository.save(product);
    }

    // Wetterdaten von der API importieren
    /*public void importWeatherDataFromApi() {
        // API-Logik hier implementieren (z.B. ApiWeatherClient verwenden)
        WeatherData weatherData = new WeatherData("Berlin", 15.5, 80.0, LocalDateTime.now());
        weatherDataRepository.save(weatherData);
    }*/

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