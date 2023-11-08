package com.example.graduatework.mapper;

import com.example.graduatework.dto.UserDto;
import com.example.graduatework.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface UserMapper {

    @Mapping(target = "image", expression =
            "java(user.getImage() != null ? \"/image/\" + user.getImage() + \"/\" : \"\")")

    UserDto userToUserDto(User user);
}