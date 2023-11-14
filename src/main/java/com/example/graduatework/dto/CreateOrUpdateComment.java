package com.example.graduatework.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CreateOrUpdateComment {

    @NotBlank
    @Size(min = 8, max = 64)
    private String text;
}
