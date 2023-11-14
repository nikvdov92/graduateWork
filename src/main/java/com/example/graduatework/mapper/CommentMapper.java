package com.example.graduatework.mapper;

import com.example.graduatework.dto.CommentDto;
import com.example.graduatework.dto.Comments;
import com.example.graduatework.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface CommentMapper {
    @Mapping(source = "id", target = "pk")
    @Mapping(source = "author.id", target = "author")
    @Mapping(source = "author.firstName", target = "authorFirstName")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "localDateTimeToLong")
    @Mapping(target = "authorImage", expression =
            "java(comment.getAuthor().getImage() != null ? \"/image/\" + comment.getAuthor().getImage() : \"\")")
    CommentDto commentToCommentDto(Comment comment);

    Comments listCommentToComments(int count, List<Comment> results);

    @Named("localDateTimeToLong")
    default Long localDateTimeToLong(LocalDateTime dateTime) {
        return dateTime.toInstant(ZonedDateTime.now().getOffset()).toEpochMilli();
    }
}
