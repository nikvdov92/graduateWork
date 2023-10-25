package com.example.graduatework.dto;


import lombok.Data;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CreateOrUpdateAd {

    @NotBlank
    @Size(min = 4, max = 32)
    private String title;

    @DecimalMin(value = "0")
    @DecimalMax(value = "10000000")
    private int price;

    @NotBlank
    @Size(min = 8, max = 64)
    private String description;
}
