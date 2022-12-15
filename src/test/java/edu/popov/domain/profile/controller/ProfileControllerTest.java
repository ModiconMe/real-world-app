package edu.popov.domain.profile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.popov.domain.account.controller.AccountController;
import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.entity.Account;
import edu.popov.domain.account.repository.AccountRepository;
import edu.popov.domain.profile.repository.FollowRelationRepository;
import edu.popov.domain.profile.service.ProfileService;
import edu.popov.security.AccountDetails;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProfileControllerTest {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FollowRelationRepository followRelationRepository;

    @Autowired
    private AccountRepository accountRepository;

    private static String Bearer1;
    private static String Bearer2;

    private ProfileController underTest;

    @BeforeEach
    void setUp() {
        underTest = new ProfileController(profileService);
    }

    @Test
    @Order(1)
    void itShouldRegisterAndSetBearerKey() throws Exception {
        accountRepository.deleteAll();
        // register
        AccountDTO.Registration request1 = AccountDTO.Registration.builder()
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .build();
        mockMvc.perform(
                        post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request1))
                                )
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Optional<Account> expected1 = accountRepository.findByEmail(request1.getEmail());
        assertThat(expected1.isPresent()).isTrue();

        AccountDTO.Registration request2 = AccountDTO.Registration.builder()
                .username("user2")
                .email("user2@gmail.com")
                .password("pass2")
                .build();
        mockMvc.perform(
                        post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request2))
                                )
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Optional<Account> expected2 = accountRepository.findByEmail(request2.getEmail());
        assertThat(expected2.isPresent()).isTrue();

        // login
        AccountDTO.Auth auth1 = AccountDTO.Auth.builder()
                .email("user1@gmail.com")
                .password("pass1")
                .build();
        ResultActions perform1 = mockMvc.perform(
                post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(auth1))
                        )
                        .accept(MediaType.APPLICATION_JSON));
        AccountDTO accountDTO1 = objectMapper.readValue(perform1.andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), AccountDTO.class);
        Bearer1 = accountDTO1.getToken();
        assertThat(Bearer1).isNotEmpty();

        AccountDTO.Auth auth2 = AccountDTO.Auth.builder()
                .email("user2@gmail.com")
                .password("pass2")
                .build();
        ResultActions perform2 = mockMvc.perform(
                post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(auth2))
                        )
                        .accept(MediaType.APPLICATION_JSON));
        AccountDTO accountDTO2 = objectMapper.readValue(perform2.andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), AccountDTO.class);
        Bearer2 = accountDTO2.getToken();
        assertThat(Bearer2).isNotEmpty();
    }

    @Test
    @Order(2)
    void itShouldGetProfile() throws Exception {
        // given
        AccountDetails accountDetails = AccountDetails.builder()
                .email("user1@gmail.com")
                .password("pass1")
                .build();
        String json = objectMapper.writeValueAsString(accountDetails);

        // when
        mockMvc
                .perform(
                        get("/api/v1/profiles/user1")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json
                                )
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("user1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.following", Matchers.is(false)));
    }

    @Test
    @Order(2)
    void itShouldNotGetProfile_whenProfileIsNotFound() throws Exception {
        // given
        AccountDetails accountDetails = AccountDetails.builder()
                .email("user1@gmail.com")
                .password("pass1")
                .build();
        String json = objectMapper.writeValueAsString(accountDetails);

        // when
        mockMvc
                .perform(
                        get("/api/v1/profiles/user3")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json
                                )
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    void itShouldFollowProfile() throws Exception {
        // given
        AccountDetails accountDetails = AccountDetails.builder()
                .email("user1@gmail.com")
                .password("pass1")
                .build();
        String json = objectMapper.writeValueAsString(accountDetails);

        // when
        mockMvc
                .perform(
                        post("/api/v1/profiles/user2/follow")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json
                                )
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("user2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.following", Matchers.is(true)));
    }

    @Test
    @Order(3)
    void itShouldNotFollowProfile_whenProfileIsNotFound() throws Exception {
        // given
        AccountDetails accountDetails = AccountDetails.builder()
                .email("user1@gmail.com")
                .password("pass1")
                .build();
        String json = objectMapper.writeValueAsString(accountDetails);

        // when
        mockMvc
                .perform(
                        post("/api/v1/profiles/user3/follow")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json
                                )
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    void itShouldGetFollowings() throws Exception {
        // given
        AccountDetails accountDetails = AccountDetails.builder()
                .email("user1@gmail.com")
                .password("pass1")
                .build();
        String json = objectMapper.writeValueAsString(accountDetails);

        // when
        mockMvc
                .perform(
                        get("/api/v1/profiles/followings")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json
                                )
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username", Matchers.is("user2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].following", Matchers.is(true)));
    }

    @Test
    @Order(5)
    void itShouldGetFollowers() throws Exception {
        // given
        AccountDetails accountDetails = AccountDetails.builder()
                .email("user2@gmail.com")
                .password("pass2")
                .build();
        String json = objectMapper.writeValueAsString(accountDetails);

        // when
        mockMvc
                .perform(
                        get("/api/v1/profiles/followers")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json
                                )
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username", Matchers.is("user1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].following", Matchers.is(false)));
    }

    @Test
    @Order(6)
    void itShouldUnfollowProfile() throws Exception {
        // given
        AccountDetails accountDetails = AccountDetails.builder()
                .email("user1@gmail.com")
                .password("pass1")
                .build();
        String json = objectMapper.writeValueAsString(accountDetails);

        // when
        mockMvc
                .perform(
                        delete("/api/v1/profiles/user2/follow")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json
                                )
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("user2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.following", Matchers.is(false)));
    }
}