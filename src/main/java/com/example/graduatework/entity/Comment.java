package com.example.graduatework.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name = "comments")

public class Comment {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String text;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    @JsonBackReference
    User author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ads_id", referencedColumnName = "id")
    @JsonBackReference
    Ad ad;

    @Override
    public String toString() {
        return text;
    }

}
