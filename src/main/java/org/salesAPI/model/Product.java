package org.salesAPI.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor; // Füge dies hinzu, wenn du @Data verwendest, da es einen NoArgsConstructor erzeugt

@Data // Erzeugt Getter, Setter, toString, equals, hashCode
@NoArgsConstructor // Stellt sicher, dass ein no-argument constructor vorhanden ist (gut für JPA und Jackson)
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore // Wichtig: Ignoriert die 'id' beim XML-Parsing, da die DB sie generiert
    private Long id;

    @JacksonXmlProperty(localName = "name") // Mappt <name> in XML zu productName
    private String productName;

    @JacksonXmlProperty(localName = "kategorie") // Mappt <kategorie> in XML zu category
    private String category;

    @JacksonXmlProperty(localName = "portionsgroesse") // Mappt <portionsgroesse> in XML zu quantity (wird später geparst)
    private int quantity; // Dies wird als String eingelesen und dann umgewandelt

    @JacksonXmlProperty(localName = "hersteller") // Mappt <hersteller> in XML zu manufacturer
    private String manufacturer;

    private String importSource;
    private LocalDateTime importDate;

    // Du kannst diesen Constructor beibehalten, er wird von Jackson nicht direkt verwendet,
    // aber kann für andere Zwecke nützlich sein. @NoArgsConstructor ist wichtiger für Jackson/JPA.
    public Product(String productName, String category, int quantity, String manufacturer, String importSource, LocalDateTime importDate) {
        this.productName = productName;
        this.category = category;
        this.quantity = quantity;
        this.manufacturer = manufacturer;
        this.importSource = importSource;
        this.importDate = importDate;
    }
}