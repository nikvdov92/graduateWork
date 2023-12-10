package com.example.graduatework.TestsController;

import com.example.graduatework.controller.AdsController;
import com.example.graduatework.dto.*;
import com.example.graduatework.entity.Ad;
import com.example.graduatework.entity.User;
import com.example.graduatework.exception.UserNotFoundException;
import com.example.graduatework.mapper.AdMapper;
import com.example.graduatework.repository.AdRepository;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Method;

import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration()
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdsControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private AdMapper adMapper;



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
        List<Method> methods = Arrays.stream(AdsController.class.getDeclaredMethods())
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
        assertEquals(methodsMap.size(), 3);
        assertEquals(methodsMap.get("removeAd")
                , "@customSecurityExpression.hasAdAuthority(authentication,#id)");
        assertEquals(methodsMap.get("updateAds")
                , "@customSecurityExpression.hasAdAuthority(authentication,#id)");
        assertEquals(methodsMap.get("updateImage")
                , "@customSecurityExpression.hasAdAuthority(authentication,#id)");
    }

    @Test
    public void getAllAdsTest() {
        ResponseEntity<Ads> getAllAdsResponseEntity =
                testRestTemplate.exchange("/ads/", HttpMethod.GET, HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {
                        });
        assertThat(getAllAdsResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(getAllAdsResponseEntity.getBody()).getResults())
                .hasSize(0);

        List<AdDto> ads = Stream.generate(() -> fakeAd(registerUser(fakeUser())))
                .limit(faker.random().nextInt(9))
                .map(adMapper::adToAdDto)
                .toList();
        getAllAdsResponseEntity =
                testRestTemplate.exchange("/ads/", HttpMethod.GET, HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {
                        });
        assertThat(getAllAdsResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(getAllAdsResponseEntity.getBody()).getResults())
                .hasSize(ads.size())
                .containsExactlyInAnyOrderElementsOf(ads);
    }

    @Test
    public void addAdTest() throws Exception {
        User user = registerUser(fakeUser());
        CreateOrUpdateAd createAd = new CreateOrUpdateAd();
        createAd.setTitle(faker.food().fruit());
        createAd.setPrice(faker.random().nextInt(9999));
        createAd.setDescription("Some description");

        MockMultipartFile image
                = new MockMultipartFile(
                "image",
                "image.jpg",
                "image/jpeg",
                new byte[1024]
        );

        MockMultipartFile body
                = new MockMultipartFile(
                "properties",
                "properties",
                "application/json",
                objectMapper.writeValueAsString(createAd).getBytes()
        );

        mockMvc.perform(multipart("/ads")
                        .file(image)
                        .file(body)
                        .with(user(user.getEmail())))
                .andExpect(result -> {
                    MockHttpServletResponse mockHttpServletResponse = result.getResponse();
                    AdDto adDtoResponse = objectMapper.readValue(mockHttpServletResponse.getContentAsString()
                            .getBytes(StandardCharsets.UTF_8), AdDto.class);
                    assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
                    assertThat(adDtoResponse).isNotNull();
                    assertThat(adDtoResponse.getTitle()).isEqualTo(createAd.getTitle());
                    assertThat(adDtoResponse.getPrice()).isEqualTo(createAd.getPrice());
                });
    }

    @Test
    public void updateAdsTest() throws Exception {
        User user = registerUser(fakeUser());
        Ad ad = fakeAd(user);
        User user1 = registerUser(fakeUser());
        Ad ad1 = fakeAd(user1);
        CreateOrUpdateAd updateAd = new CreateOrUpdateAd();
        updateAd.setTitle(faker.food().fruit());
        updateAd.setPrice(faker.random().nextInt(9999));
        updateAd.setDescription("Some description");

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/ads/" + ad.getId())
                        .with(user(user.getEmail()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAd)))
                .andExpect(result -> {
                    MockHttpServletResponse mockHttpServletResponse = result.getResponse();
                    AdDto adDtoResponse = objectMapper.readValue(mockHttpServletResponse.getContentAsString()
                            .getBytes(StandardCharsets.UTF_8), AdDto.class);
                    assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
                    assertThat(adDtoResponse).isNotNull();
                    assertThat(adDtoResponse.getTitle()).isEqualTo(updateAd.getTitle());
                    assertThat(adDtoResponse.getPrice()).isEqualTo(updateAd.getPrice());
                });

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/ads/" + ad1.getId())
                        .with(user(user.getEmail()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAd)))
                .andExpect(status().isForbidden());

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/ads/" + ad1.getId())
                        .with(user(user1.getEmail()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAd)))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/ads/" + ad.getId())
                        .with(user(user1.getEmail()).roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAd)))
                .andExpect(status().isForbidden());

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/ads/" + 9)
                        .with(user(user.getEmail()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAd)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getExtendedAdTest() throws Exception {
        User user = registerUser(fakeUser());
        Ad ad = fakeAd(user);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/ads/" + ad.getId())
                        .with(user(user.getEmail())))
                .andExpect(result -> {
                    MockHttpServletResponse mockHttpServletResponse = result.getResponse();
                    ExtendedAd extendedAd = objectMapper.readValue(mockHttpServletResponse.getContentAsString()
                            .getBytes(StandardCharsets.UTF_8), ExtendedAd.class);
                    assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
                    assertThat(extendedAd).isNotNull();
                    assertThat(extendedAd.getAuthorFirstName()).isEqualTo(ad.getAuthor().getFirstName());
                    assertThat(extendedAd.getAuthorLastName()).isEqualTo(ad.getAuthor().getLastName());
                    assertThat(extendedAd.getDescription()).isEqualTo(ad.getDescription());
                    assertThat(extendedAd.getEmail()).isEqualTo(ad.getAuthor().getEmail());
                    assertThat(extendedAd.getPhone()).isEqualTo(ad.getAuthor().getPhone());
                    assertThat(extendedAd.getPrice()).isEqualTo(ad.getPrice());
                    assertThat(extendedAd.getTitle()).isEqualTo(ad.getTitle());
                });

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/ads/" + 9)
                        .with(user(user.getEmail())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAdsMeTest() throws Exception {
        User user = registerUser(fakeUser());
        User user1 = registerUser(fakeUser());
        List<AdDto> userAds = Stream.generate(() -> fakeAd(user))
                .limit(faker.random().nextInt(9))
                .map(adMapper::adToAdDto)
                .toList();
        List<AdDto> user1Ads = Stream.generate(() -> fakeAd(user1))
                .limit(faker.random().nextInt(9))
                .map(adMapper::adToAdDto)
                .toList();
        ResponseEntity<Ads> getAllAdsResponseEntity =
                testRestTemplate.exchange("/ads/", HttpMethod.GET, HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {
                        });
        assertThat(getAllAdsResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(getAllAdsResponseEntity.getBody()).getResults())
                .hasSize(userAds.size() + user1Ads.size());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/ads/me")
                        .with(user(user.getEmail()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    MockHttpServletResponse mockHttpServletResponse = result.getResponse();
                    assertThat(mockHttpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
                    String json = mockHttpServletResponse.getContentAsString();
                    assertEquals(objectMapper.readTree(json).get("count").asInt(), userAds.size());
                });
    }

    @Test
    public void removeAdTest() throws Exception {
        User user = registerUser(fakeUser());
        User user1 = registerUser(fakeUser());
        Ad ad = fakeAd(user);
        Ad ad1 = fakeAd(user);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/ads/" + ad.getId())
                        .with(user(user.getEmail())))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/ads/" + ad.getId())
                        .with(user(user.getEmail())))
                .andExpect(status().isNotFound());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/ads/" + ad1.getId())
                        .with(user(user1.getEmail())))
                .andExpect(status().isForbidden());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/ads/" + ad1.getId())
                        .with(user(user1.getEmail()).roles("ADMIN")))
                .andExpect(status().isForbidden());
    }

    private Ad fakeAd(User user) {
        Ad ad = new Ad();
        ad.setTitle(faker.food().fruit());
        ad.setPrice(faker.random().nextInt(9999));
        ad.setDescription("Some description");
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

    private User registerUser(Register register) {
        ResponseEntity<User> registerResponseEntity
                = testRestTemplate.postForEntity("/register", register, User.class);
        assertThat(registerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return userRepository.findUserByEmail(register.getUsername())
                .orElseThrow(UserNotFoundException::new);
    }
}
