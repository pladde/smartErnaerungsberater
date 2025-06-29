package org.salesAPI.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
public class XmlParser {

    private static final Logger logger = LoggerFactory.getLogger(XmlParser.class);
    private final XmlMapper xmlMapper;
    private final ObjectMapper jsonMapper; // For converting JsonNode to Product

    public XmlParser() {
        this.xmlMapper = new XmlMapper();
        this.jsonMapper = new ObjectMapper();
        // Wenn ein Feld in der Java-Klasse nicht in der XML vorhanden ist,
        // und es nicht @JsonIgnore ist, würde dies einen Fehler werfen.
        // Mit dieser Einstellung werden diese Fehler vermieden.
        this.xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Ermöglicht das Parsen von XML-Dateien, selbst wenn sie keine explizite XML-Deklaration haben
        this.xmlMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    }

    public List<Product> parseXmlToProducts(MultipartFile file) {
        Objects.requireNonNull(file, "Die hochgeladene Datei darf nicht null sein.");
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Die hochgeladene XML-Datei ist leer.");
        }

        List<Product> parsedProducts = new ArrayList<>();

        try {
            // Lese die gesamte Datei als String
            String xmlContent = new String(file.getBytes());
            logger.debug("XML Content: \\n{}", xmlContent); // Log the XML content

            // Parse the XML into a JsonNode tree
            JsonNode root = xmlMapper.readTree(xmlContent);

            // Navigate to the 'lebensmittel' elements
            JsonNode lebensmittelList = root.get("lebensmittelliste").get("lebensmittel");
            if (lebensmittelList.isArray()) {
                for (JsonNode lebensmittelNode : lebensmittelList) {
                    try {
                        // Manually extract the fields from the JsonNode and map them to Product
                        Product product = new Product();
                        product.setProductName(lebensmittelNode.get("name").asText());
                        product.setCategory(lebensmittelNode.get("kategorie").asText());

                        // Handle portionsgroesse
                        String portionsgroesse = lebensmittelNode.get("portionsgroesse").asText();
                        if (portionsgroesse != null && !portionsgroesse.isEmpty()) {
                            String numericString = portionsgroesse.replaceAll("[^\\d.]", "");
                            if (!numericString.isEmpty()) {
                                product.setQuantity(Integer.parseInt(numericString));
                            } else {
                                product.setQuantity(0); // Default value
                                logger.warn("Ungültige Zahl für 'portionsgroesse'. Setze Menge auf 0.");
                            }
                        } else {
                            product.setQuantity(0); // Default value
                            logger.warn("'portionsgroesse' ist leer. Setze Menge auf 0.");
                        }

                        product.setManufacturer(lebensmittelNode.get("hersteller").asText());

                        parsedProducts.add(product);

                    } catch (Exception e) {
                        logger.warn("WARNUNG: Fehler beim Verarbeiten eines Lebensmittel-Elements. Datensatz wird übersprungen. Fehler: {}", e.getMessage());
                    }
                }
            }


            // Setze Import-Metadaten für jedes Produkt
            for (Product product : parsedProducts) {
                product.setImportSource("XML_Uploaded");
                product.setImportDate(LocalDateTime.now());
            }

            logger.info("Erfolgreich {} Produkte aus XML-Datei '{}' geparst.", parsedProducts.size(), file.getOriginalFilename());
            return parsedProducts;

        } catch (IOException e) {
            String errorMessage = "Fehler beim Verarbeiten der XML-Datei '" + file.getOriginalFilename() + "': " + e.getMessage();
            logger.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        } catch (Exception e) {
            logger.error("FEHLER: Unerwarteter Fehler beim Parsen der XML-Datei '{}'. Fehler: {}",
                    file.getOriginalFilename(), e.getMessage(), e);
            throw new RuntimeException("Fehler beim Parsen der XML-Datei: " + e.getMessage(), e);
        }
    }
}