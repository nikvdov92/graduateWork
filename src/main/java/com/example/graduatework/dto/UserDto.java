package com.example.graduatework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UserDto {
    private int id;
    @Email(regexp = ".+@.+[.]..+")
    @Schema(example = "user@user.com")
    private String email;

    @NotBlank
    @Size(min = 2, max = 16)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 16)
    private String lastName;
    @Pattern(regexp = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}")
    private String phone;
    private Role role;

    private String image;

}
