package com.example.graduatework.dto;


import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewPassword {

    @NotBlank
    @Size(min = 8, max = 16)
    private String currentPassword;

    @NotBlank
    @Size(min = 8, max = 16)
    private String newPassword;
}
