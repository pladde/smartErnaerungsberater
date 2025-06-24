package org.salesAPI.util;

import org.salesAPI.model.Product;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CsvParser {

    private static final Logger logger = LoggerFactory.getLogger(CsvParser.class);

    private static final String[] CSV_HEADERS = {
            "name",
            "kategorie",
            "portionsgroesse",
            "hersteller"
    };

    public List<Product> parseCsvToProducts(MultipartFile file) {
        Objects.requireNonNull(file, "Die hochgeladene Datei darf nicht null sein.");
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Die hochgeladene CSV-Datei ist leer.");
        }

        List<Product> products = new ArrayList<>();

        CSVFormat csvFormat = CSVFormat.DEFAULT
                .withFirstRecordAsHeader() // Wichtig, um die Header-Zeile zu überspringen
                .withIgnoreHeaderCase()    // Groß-/Kleinschreibung der Header wird ignoriert
                .withTrim()                // Leerzeichen an den Enden werden entfernt
                .withDelimiter(',')        // Trennzeichen ist ein Komma
                .withQuote('"')            // Anführungszeichen für Felder mit Kommas etc.
                .withAllowMissingColumnNames(true); // Vorsicht: Wenn ein essentieller Header fehlt, kann das zu Fehlern beim Zugriff führen

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, csvFormat)) {

            // Überprüfung, ob alle erforderlichen Header vorhanden sind
            List<String> foundHeadersNormalized = csvParser.getHeaderNames().stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            List<String> missingRequiredHeaders = new ArrayList<>();
            for (String requiredHeader : CSV_HEADERS) {
                if (!foundHeadersNormalized.contains(requiredHeader.toLowerCase())) {
                    missingRequiredHeaders.add(requiredHeader);
                }
            }

            if (!missingRequiredHeaders.isEmpty()) {
                String errorMessage = String.format(
                        "Fehlende erforderliche Spalten in der CSV-Datei: %s. " +
                                "Bitte stellen Sie sicher, dass alle benötigten Header vorhanden sind. " +
                                "Erwartet (Groß-/Kleinschreibung ignoriert): %s. Gefunden: %s.",
                        String.join(", ", missingRequiredHeaders),
                        String.join(", ", CSV_HEADERS),
                        String.join(", ", csvParser.getHeaderNames())
                );
                logger.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            for (CSVRecord csvRecord : csvParser) {
                long recordNumber = csvRecord.getRecordNumber();
                try {

                    Product product = new Product();

                    product.setProductName(csvRecord.get("name"));
                    product.setCategory(csvRecord.get("kategorie"));
                    // Die "portionsgroesse" enthält "g". Nur die Zahl wird heir geparst
                    String quantityString = csvRecord.get("portionsgroesse").replaceAll("[^\\d.]", ""); // Entfernt alles außer Zahlen und Punkte
                    product.setQuantity(Integer.parseInt(quantityString));
                    product.setManufacturer(csvRecord.get("hersteller"));
                    product.setImportSource("CSV_Uploaded");
                    product.setImportDate(LocalDateTime.now());

                    products.add(product);

                } catch (NumberFormatException e) {
                    logger.warn("WARNUNG: Ungültige Zahl für 'portionsgroesse' in CSV-Zeile {} ('{}'). Datensatz wird übersprungen. Fehler: {}",
                            recordNumber, csvRecord.get("portionsgroesse"), e.getMessage());
                } catch (IllegalArgumentException e) {

                    logger.warn("WARNUNG: Fehler beim Zugriff auf CSV-Spalte in Zeile {}. Möglicherweise fehlende Spalte oder falsch benannt. Datensatz wird übersprungen. Fehler: {}",
                            recordNumber, e.getMessage());
                } catch (Exception e) {
                    logger.error("FEHLER: Unerwarteter Fehler beim Parsen von CSV-Zeile {}. Datensatz wird übersprungen. Fehler: {}",
                            recordNumber, e.getMessage(), e);
                }
            }
            logger.info("Erfolgreich {} Produkte aus CSV-Datei '{}' geparst.", products.size(), file.getOriginalFilename());
            return products;

        } catch (IOException e) {
            String errorMessage = "Fehler beim Verarbeiten der CSV-Datei '" + file.getOriginalFilename() + "': " + e.getMessage();
            logger.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }
}