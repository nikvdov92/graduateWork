package com.example.graduatework.entity;
import lombok.Data;


import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "ads")
public class AdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private Integer price;
    private String image;
    private String description;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private UserEntity author;

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL)
    private List<CommentEntity> commentEntities;
}