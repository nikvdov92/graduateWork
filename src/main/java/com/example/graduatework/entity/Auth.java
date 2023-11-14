package com.example.graduatework.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "auth")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String username;

    String authority;

    @OneToOne(mappedBy = "auth")
    User user;
}
