package com.example.graduatework.TestsController;

import com.example.graduatework.dto.*;
import com.example.graduatework.entity.User;
import com.example.graduatework.exception.UserNotFoundException;
import com.example.graduatework.mapper.UserMapper;
import com.example.graduatework.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.github.javafaker.Faker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration()
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsersControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;
    private final Faker faker = new Faker();
    private MockMvc mockMvc;

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    public void setPasswordTest() throws Exception {
        Register register = fakeUser();
        NewPassword newPassword = new NewPassword();
        newPassword.setCurrentPassword(register.getPassword());
        newPassword.setNewPassword("87654321");
        User user = registerUser(register);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/set_password")
                        .with(user(user.getEmail()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPassword)))
                .andExpect(status().isOk());
        User updatedUser = userRepository.findUserByEmail(user.getEmail()).orElseThrow(UserNotFoundException::new);
        assertNotEquals(user.getPassword(), updatedUser.getPassword());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/set_password")
                        .with(user(user.getEmail()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPassword)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getUserTest() throws Exception {
        User user = registerUser(fakeUser());
        UserDto userDto = userMapper.userToUserDto(user);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/me")
                        .with(user(user.getEmail())))
                .andExpect(result -> {
                    MockHttpServletResponse mockHttpServletResponse = result.getResponse();
                    UserDto userDtoResult = objectMapper.readValue(mockHttpServletResponse.getContentAsString()
                            .getBytes(StandardCharsets.UTF_8), UserDto.class);
                    assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
                    assertThat(userDtoResult).isNotNull();
                    assertThat(userDtoResult).isEqualTo(userDto);
                });
    }

    @Test
    public void updateUserTest() throws Exception {
        User user = registerUser(fakeUser());
        UpdateUser updateUser = new UpdateUser();
        updateUser.setFirstName(faker.name().firstName());
        updateUser.setLastName(faker.name().lastName());
        updateUser.setPhone("+71234567890");

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/users/me")
                        .with(user(user.getEmail()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(result -> {
                    MockHttpServletResponse mockHttpServletResponse = result.getResponse();
                    assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());

                    String jsonResponse = mockHttpServletResponse.getContentAsString();
                    UserDto updatedUser = objectMapper.readValue(jsonResponse, UserDto.class);

                    assertThat(updatedUser).isNotNull();
                    assertThat(updatedUser.getFirstName()).isEqualTo(updateUser.getFirstName());
                    assertThat(updatedUser.getLastName()).isEqualTo(updateUser.getLastName());
                    assertThat(updatedUser.getPhone()).isEqualTo(updateUser.getPhone());
                });

    }

    private Register fakeUser() {
        Register register = new Register();
        register.setUsername(faker.internet().emailAddress());
        register.setPassword(faker.internet().password());
        register.setFirstName(faker.name().firstName());
        register.setLastName(faker.name().lastName());
        register.setPhone(faker.phoneNumber().phoneNumber());
        register.setRole(Role.USER);
        return register;
    }

    private User registerUser(Register register) {
        ResponseEntity<User> registerResponseEntity
                = testRestTemplate.postForEntity("/register", register, User.class);
        assertThat(registerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return userRepository.findUserByEmail(register.getUsername())
                .orElseThrow(UserNotFoundException::new);
    }
}
