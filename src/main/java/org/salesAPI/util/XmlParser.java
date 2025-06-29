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
    private final ObjectMapper jsonMapper;

    public XmlParser() {
        this.xmlMapper = new XmlMapper();
        this.jsonMapper = new ObjectMapper();
        this.xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.xmlMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    }

    public List<Product> parseXmlToProducts(MultipartFile file) {
        Objects.requireNonNull(file, "Die hochgeladene Datei darf nicht null sein.");
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Die hochgeladene XML-Datei ist leer.");
        }

        List<Product> parsedProducts = new ArrayList<>();

        try {
            String xmlContent = new String(file.getBytes());
            logger.debug("XML Content: \\n{}", xmlContent);

            JsonNode root = xmlMapper.readTree(xmlContent);

            JsonNode lebensmittelNodes = root.get("lebensmittel");

            if (lebensmittelNodes != null && lebensmittelNodes.isArray()) {
                for (JsonNode lebensmittelNode : lebensmittelNodes) {
                    try {
                        Product product = new Product();
                        product.setProductName(lebensmittelNode.get("name").asText());
                        product.setCategory(lebensmittelNode.get("kategorie").asText());

                        String portionsgroesse = lebensmittelNode.get("portionsgroesse").asText();
                        if (portionsgroesse != null && !portionsgroesse.isEmpty()) {
                            String numericString = portionsgroesse.replaceAll("[^\\d.]", "");
                            if (!numericString.isEmpty()) {
                                product.setQuantity(Integer.parseInt(numericString));
                            } else {
                                product.setQuantity(0);
                                logger.warn("Ungültige Zahl für 'portionsgroesse'. Setze Menge auf 0.");
                            }
                        } else {
                            product.setQuantity(0);
                            logger.warn("'portionsgroesse' ist leer. Setze Menge auf 0.");
                        }

                        product.setManufacturer(lebensmittelNode.get("hersteller").asText());

                        parsedProducts.add(product);

                    } catch (Exception e) {
                        logger.warn("WARNUNG: Fehler beim Verarbeiten eines Lebensmittel-Elements. Datensatz wird übersprungen. Fehler: {}", e.getMessage());
                    }
                }
            } else if (lebensmittelNodes != null && !lebensmittelNodes.isArray() && lebensmittelNodes.isObject()) {
                try {
                    Product product = new Product();
                    product.setProductName(lebensmittelNodes.get("name").asText());
                    product.setCategory(lebensmittelNodes.get("kategorie").asText());
                    String portionsgroesse = lebensmittelNodes.get("portionsgroesse").asText();
                    if (portionsgroesse != null && !portionsgroesse.isEmpty()) {
                        String numericString = portionsgroesse.replaceAll("[^\\d.]", "");
                        if (!numericString.isEmpty()) {
                            product.setQuantity(Integer.parseInt(numericString));
                        } else {
                            product.setQuantity(0);
                            logger.warn("Ungültige Zahl für 'portionsgroesse'. Setze Menge auf 0.");
                        }
                    } else {
                        product.setQuantity(0);
                        logger.warn("'portionsgroesse' ist leer. Setze Menge auf 0.");
                    }
                    product.setManufacturer(lebensmittelNodes.get("hersteller").asText());
                    parsedProducts.add(product);
                } catch (Exception e) {
                    logger.warn("WARNUNG: Fehler beim Verarbeiten eines EINZELNEN Lebensmittel-Elements. Datensatz wird übersprungen. Fehler: {}", e.getMessage());
                }
            } else {
                logger.error("FEHLER: 'lebensmittel'-Element(e) wurden nicht gefunden oder sind nicht im erwarteten Format (Array/Object).");
            }

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