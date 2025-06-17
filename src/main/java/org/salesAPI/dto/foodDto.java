package org.salesAPI.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class foodDto {

    private Long id;
    private String productName;
    private String catergory;
    private int quantity;
    private String manufactor;
    private String importSource;
    private LocalDateTime importDate;
}