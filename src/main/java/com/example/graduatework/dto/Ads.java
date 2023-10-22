package com.example.graduatework.dto;

import lombok.Data;

import java.util.List;

@Data
public class Ads {
    private int count;
    private List<AdDto> results;
}
