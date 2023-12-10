package com.example.graduatework.TestsController;

import com.example.graduatework.controller.CommentsController;
import com.example.graduatework.dto.*;
import com.example.graduatework.entity.Ad;
import com.example.graduatework.entity.Comment;
import com.example.graduatework.entity.User;
import com.example.graduatework.exception.UserNotFoundException;
import com.example.graduatework.mapper.CommentMapper;
import com.example.graduatework.repository.AdRepository;
import com.example.graduatework.repository.CommentRepository;
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
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Method;

import java.nio.charset.StandardCharsets;

import java.time.LocalDateTime;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration()
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentsControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentMapper commentMapper;

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
        adRepository.deleteAll();
    }

    @Test
    public void preAuthorizeAnnotationValueTest() {
        List<Method> methods = Arrays.stream(CommentsController.class.getDeclaredMethods())
                .filter(method -> AnnotationUtils.getAnnotation(method, PreAuthorize.class) != null)
                .toList();
        Map<String, String> methodsMap = methods.stream()
                .peek(method -> {
                    PreAuthorize annotation = AnnotationUtils.getAnnotation(method, PreAuthorize.class);
                    assert annotation != null;
                })
                .collect(Collectors.toMap(
                        Method::getName,
                        method -> Objects.requireNonNull(AnnotationUtils.getAnnotation(method, PreAuthorize.class))
                                .value()
                ));
        assertEquals(methodsMap.size(), 2);
        assertEquals(methodsMap.get("deleteComment")
                , "@customSecurityExpression.hasCommentAuthority(authentication,#commentId )");
        assertEquals(methodsMap.get("updateComment")
                , "@customSecurityExpression.hasCommentAuthority(authentication,#commentId )");
    }

    @Test
    public void getComments() throws Exception {
        User user = registerUser(fakeUser());
        Ad ad = fakeAd(user);
        List<CommentDto> comments = Stream.generate(() -> fakeComment(registerUser(fakeUser()), ad))
                .limit(faker.random().nextInt(9))
                .map(commentMapper::commentToCommentDto)
                .toList();
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/ads/" + ad.getId() + "/comments")
                        .with(user(user.getEmail())))
                .andExpect(result -> {
                    MockHttpServletResponse mockHttpServletResponse = result.getResponse();
                    Comments commentsResponse = objectMapper.readValue(mockHttpServletResponse
                            .getContentAsString().getBytes(StandardCharsets.UTF_8), Comments.class);
                    assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
                    assertThat(commentsResponse).isNotNull();
                    assertThat(commentsResponse.getResults())
                            .hasSize(comments.size())
                            .usingRecursiveFieldByFieldElementComparator()
                            .containsExactlyInAnyOrderElementsOf(comments);
                });
    }

    @Test
    public void addCommentTest() throws Exception {
        User user = registerUser(fakeUser());
        Ad ad = fakeAd(user);
        CreateOrUpdateComment createComment = new CreateOrUpdateComment();
        createComment.setText(faker.book().title());
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/ads/" + ad.getId() + "/comments")
                        .with(user(user.getEmail()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createComment))
                )
                .andExpect(result -> {
                    MockHttpServletResponse mockHttpServletResponse = result.getResponse();
                    CommentDto createCommentResult = objectMapper.readValue(mockHttpServletResponse
                            .getContentAsString().getBytes(StandardCharsets.UTF_8), CommentDto.class);
                    assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
                    assertThat(createCommentResult).isNotNull();
                    assertThat(createCommentResult.getText()).isEqualTo(createComment.getText());
                });
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/ads/9/comments")
                        .with(user(user.getEmail()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createComment))
                )
                .andExpect(status().isNotFound());
    }
    @Test
    public void removeCommentTest() throws Exception {
        User user = registerUser(fakeUser());
        User user1 = registerUser(fakeUser());
        Ad ad = fakeAd(user);
        Ad ad1 = fakeAd(user);
        Comment comment = fakeComment(user, ad);
        Comment comment1 = fakeComment(user, ad);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/ads/" + ad.getId() + "/comments/" + comment.getId())
                        .with(user(user.getEmail())))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/ads/" + ad.getId() + "/comments/" + comment.getId())
                        .with(user(user.getEmail())))
                .andExpect(status().isNotFound());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/ads/" + ad1.getId() + "/comments/" + comment1.getId())
                        .with(user(user.getEmail())))
                .andExpect(status().isNotFound());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/ads/" + ad.getId() + "/comments/" + comment1.getId())
                        .with(user(user1.getEmail())))
                .andExpect(status().isForbidden());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/ads/" + ad.getId() + "/comments/" + comment1.getId())
                        .with(user(user1.getEmail()).roles("ADMIN")))
                .andExpect(status().isForbidden());
    }

    private Ad fakeAd(User user) {
        Ad ad = new Ad();
        ad.setTitle(faker.food().fruit());
        ad.setPrice(faker.random().nextInt(9999));
        ad.setDescription(faker.funnyName().name());
        ad.setAuthor(user);
        ad.setImage("testImage");
        return adRepository.save(ad);
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

    private Comment fakeComment(User user, Ad ad) {
        Comment comment = new Comment();
        comment.setText(faker.book().title());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setAd(ad);
        return commentRepository.save(comment);
    }

    private User registerUser(Register register) {
        ResponseEntity<User> registerResponseEntity
                = testRestTemplate.postForEntity("/register", register, User.class);
        assertThat(registerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return userRepository.findUserByEmail(register.getUsername())
                .orElseThrow(UserNotFoundException::new);
    }
}
