package com.example.graduatework.entity;

import com.example.graduatework.dto.Ad;
import com.example.graduatework.dto.Comment;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import com.example.graduatework.dto.Role;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.List;
@Entity
@Table(name = "users")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @EqualsAndHashCode.Include
    @Email
    String email;

    String password;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    String phone;

    @Enumerated(EnumType.STRING)
    Role role;

    String image;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @JsonManagedReference
    List<Ad> ads;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @JsonManagedReference
    List<Comment> comments;
}
