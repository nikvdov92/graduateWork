package com.example.graduatework.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@Builder
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name = "ads")

public class Ad {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private Integer price;
    private String image;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "author_id", referencedColumnName = "id")
    @JsonBackReference
    User author;

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL)
    @JsonManagedReference
    List<Comment> comments;

    @Override
    public String toString() {
        return title;
    }
}