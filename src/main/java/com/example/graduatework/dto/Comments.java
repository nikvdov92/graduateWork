package com.example.graduatework.dto;

import lombok.Data;
import java.util.List;

@Data
public class Comments {
    private int count;
    private List<CommentDto> results;
}
