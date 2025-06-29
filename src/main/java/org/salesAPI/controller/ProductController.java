package org.salesAPI.controller;

import org.salesAPI.model.Product;
import org.salesAPI.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products") // Hier der Pfad zu den Produkten
public class ProductController {

    @Autowired
    private ProductService productService;

    // POST: Produkt hinzufügen
    @PostMapping
    public Product saveProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    // GET: Alle Produkte abrufen
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // GET: Produkt nach ID abrufen
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // PUT: Produkt aktualisieren
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        return productService.updateProduct(id, updatedProduct);
    }

    // DELETE: Produkt löschen
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    // POST: CSV Import
    @PostMapping("/csv")
    public String uploadCsv(@RequestParam("file") MultipartFile file) {
        productService.importCSV(file);
        return "CSV Import erfolgreich!";
    }

    // POST: XML Import
    @PostMapping("/xml")
    public String uploadXml(@RequestParam("file") MultipartFile file) {
        productService.importXml(file);
        return "XML Import erfolgreich!";
    }
}