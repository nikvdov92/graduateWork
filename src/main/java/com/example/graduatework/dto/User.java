package com.example.graduatework.dto;


import lombok.Data;

@Data
public class User {
    private Integer id;
    private String email;
    private String firstname;
    private String lastName;
    private String phone;
    private String role;
    private String image;

}
