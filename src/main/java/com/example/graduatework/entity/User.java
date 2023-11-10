package com.example.graduatework.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
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

    boolean enabled;

    LocalDateTime regDate;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @JsonManagedReference
    List<Ad> ads;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @JsonManagedReference
    List<Comment> comments;

    @OneToOne
    @JoinColumn(name = "id")
    Auth auth;
}
