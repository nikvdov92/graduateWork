package com.example.graduatework.TestsController;

import com.example.graduatework.dto.Login;
import com.example.graduatework.dto.Register;
import com.example.graduatework.dto.Role;
import com.example.graduatework.entity.User;
import com.example.graduatework.exception.UserNotFoundException;
import com.example.graduatework.repository.UserRepository;

import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthControllerTests {
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

    public class AuthControllerTest {
        @LocalServerPort
        private int port;

        @Autowired
        private TestRestTemplate testRestTemplate;

        @Autowired
        private UserRepository userRepository;
        private UsernamePasswordAuthenticationToken principal;
        private final Faker faker = new Faker();

        @BeforeEach
        public void beforeEach() {
            User user = registerUser(fakeUser());
            principal = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
            SecurityContextHolder.getContext().setAuthentication(principal);
        }

        @AfterEach
        public void afterEach() {
            userRepository.deleteAll();
        }

        @ParameterizedTest
        @MethodSource("usersForLogin")
        public void loginTest(String email, String password, HttpStatus httpStatus) {
            Register register = fakeUser();
            register.setUsername("user1@nikvdov.com");
            register.setPassword("12345678");
            User user = registerUser(register);
            assertThat(user.getEmail()).isEqualTo(register.getUsername());
            principal = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
            SecurityContextHolder.getContext().setAuthentication(principal);
            Login login = new Login();
            login.setUsername(email);
            login.setPassword(password);
            ResponseEntity<User> loginResponseEntity = loginUser(login);
            assertThat(loginResponseEntity.getStatusCode()).isEqualTo(httpStatus);
        }

        @Test
        public void registerTest() {
            Register register = fakeUser();
            ResponseEntity<User> registerResponseEntity = registerUserRequest(register);
            assertThat(registerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            register = fakeUser();
            registerUser(register);
            assertThat(registerUserRequest(register).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        private ResponseEntity<User> loginUser(Login login) {
            return testRestTemplate.postForEntity("http://localhost:" + port + "/login", login, User.class);
        }

        private ResponseEntity<User> registerUserRequest(Register register) {
            return testRestTemplate.postForEntity("http://localhost:" + port + "/register", register, User.class);
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
            ResponseEntity<User> registerResponseEntity = registerUserRequest(register);
            assertThat(registerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            return userRepository.findUserByEmail(register.getUsername())
                    .orElseThrow(UserNotFoundException::new);
        }

        public Stream<Arguments> usersForLogin() {
            return Stream.of(
                    Arguments.of("user1@nikvdov.com", "03051996", HttpStatus.OK),
                    Arguments.of("user2@nikvdov.com", "03051997", HttpStatus.UNAUTHORIZED),
                    Arguments.of("user3@nikvdov.com", "03051998", HttpStatus.UNAUTHORIZED)
            );
        }
    }
}

