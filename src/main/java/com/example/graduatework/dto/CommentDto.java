package com.example.graduatework.dto;


import lombok.Data;

@Data
public class CommentDto {
    private int author;

    private String authorImage;

    private String authorFirstName;

    private long createdAt;

    private int pk;

    private String text;

}
