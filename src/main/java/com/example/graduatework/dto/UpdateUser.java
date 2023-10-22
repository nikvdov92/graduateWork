package com.example.graduatework.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UpdateUser {

    @NotBlank
    @Size(min = 2, max = 16)
    private String firstname;

    @NotBlank
    @Size(min = 2, max = 16)
    private String lastName;

    @Pattern(regexp = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}")
    private String phone;

}
