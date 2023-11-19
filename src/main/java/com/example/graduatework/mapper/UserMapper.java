package com.example.graduatework.mapper;

import com.example.graduatework.dto.Register;
import com.example.graduatework.dto.UserDto;
import com.example.graduatework.entity.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "image", expression = "java(user.getImage() != null ? \"/image/\" + user.getImage() : \"\")")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.id", target = "id")
    UserDto userToUserDto(User user);

    @Mapping(source = "register.username", target = "email")
    User registerToUser(Register register);
}

