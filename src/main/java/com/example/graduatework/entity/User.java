package com.example.graduatework.entity;

import com.example.graduatework.dto.AdDto;
import com.example.graduatework.dto.CommentDto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.example.graduatework.dto.Role;

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
    List<AdDto> ads;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @JsonManagedReference
    List<CommentDto> comments;
}
