package com.example.graduatework.entity;

import com.example.graduatework.dto.Role;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

import javax.validation.constraints.Email;

import java.util.List;
@AllArgsConstructor
@Builder
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name = "users")

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @EqualsAndHashCode.Include
    @Column(name = "username")
    @Email
    String email;

    String password;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    String phone;

    String image;

    Role role;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    List<Ad> ads;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    List<Comment> comments;

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
