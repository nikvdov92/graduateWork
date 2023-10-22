package com.example.graduatework.entity;

import com.example.graduatework.dto.AdDto;
import com.example.graduatework.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String text;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    @JsonIgnore
    private UserDto author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ads_id", referencedColumnName = "id")
    @JsonIgnore
    private AdDto adDto;

}
