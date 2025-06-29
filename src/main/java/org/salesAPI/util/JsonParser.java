package org.salesAPI.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.salesAPI.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class JsonParser {

    private static final Logger logger = LoggerFactory.getLogger(JsonParser.class);
    private final ObjectMapper objectMapper;

    public JsonParser() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<Product> parseJsonToProducts(MultipartFile file) {
        Objects.requireNonNull(file, "Die hochgeladene Datei darf nicht null sein.");
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Die hochgeladene JSON-Datei ist leer.");
        }

        List<Product> parsedProducts = new ArrayList<>();

        try {
            String jsonContent = new String(file.getBytes());
            logger.debug("JSON Content: \\n{}", jsonContent);

            JsonNode root = objectMapper.readTree(jsonContent);

            // Hier navigieren wir zu "lebensmittel", wie in deinem JSON-Beispiel
            JsonNode lebensmittelArray = root.get("lebensmittel");

            if (lebensmittelArray != null && lebensmittelArray.isArray()) {
                for (JsonNode lebensmittelNode : lebensmittelArray) {
                    try {
                        Product product = new Product();
                        // Wichtig: JSON-Parsing kann oft direkter sein, wenn Feldnamen im Model und JSON gleich sind
                        // Aber da dein Product Model 'productName' statt 'name' hat, müssen wir manuell mappen.
                        // Alternativ: Nutze @JsonProperty("name") im Product Model für die JSON-Felder.
                        product.setProductName(lebensmittelNode.get("name").asText());
                        product.setCategory(lebensmittelNode.get("kategorie").asText());

                        String portionsgroesse = lebensmittelNode.get("portionsgroesse").asText();
                        if (portionsgroesse != null && !portionsgroesse.isEmpty()) {
                            String numericString = portionsgroesse.replaceAll("[^\\d.]", "");
                            if (!numericString.isEmpty()) {
                                product.setQuantity(Integer.parseInt(numericString));
                            } else {
                                product.setQuantity(0);
                                logger.warn("Ungültige Zahl für 'portionsgroesse' im JSON. Setze Menge auf 0.");
                            }
                        } else {
                            product.setQuantity(0);
                            logger.warn("'portionsgroesse' ist leer im JSON. Setze Menge auf 0.");
                        }

                        product.setManufacturer(lebensmittelNode.get("hersteller").asText());
                        product.setImportSource("JSON_Uploaded");
                        product.setImportDate(LocalDateTime.now());

                        parsedProducts.add(product);
                    } catch (Exception e) {
                        logger.warn("WARNUNG: Fehler beim Verarbeiten eines Lebensmittel-Elements aus JSON. Datensatz wird übersprungen. Fehler: {}", e.getMessage());
                    }
                }
            } else {
                logger.error("FEHLER: 'lebensmittel'-Array nicht gefunden oder ist kein Array im JSON.");
            }

            logger.info("Erfolgreich {} Produkte aus JSON-Datei '{}' geparst.", parsedProducts.size(), file.getOriginalFilename());
            return parsedProducts;

        } catch (IOException e) {
            String errorMessage = "Fehler beim Verarbeiten der JSON-Datei '" + file.getOriginalFilename() + "': " + e.getMessage();
            logger.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        } catch (Exception e) {
            logger.error("FEHLER: Unerwarteter Fehler beim Parsen der JSON-Datei '{}'. Fehler: {}",
                    file.getOriginalFilename(), e.getMessage(), e);
            throw new RuntimeException("Fehler beim Parsen der JSON-Datei: " + e.getMessage(), e);
        }
    }
}