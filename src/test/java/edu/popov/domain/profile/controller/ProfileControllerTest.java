package edu.popov.domain.profile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.account.repository.AccountRepository;
import edu.popov.domain.profile.repository.FollowRelationRepository;
import edu.popov.domain.profile.service.ProfileService;
import edu.popov.security.AccountDetails;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Objects;
import java.util.Optional;

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

    @Test
    @Order(1)
    void itShouldRegisterAndSetBearerKey() throws Exception {
//        accountRepository.deleteAll();
        // register
        AccountDTO.Registration request1 = AccountDTO.Registration.builder()
                .username("profiletest1")
                .email("profiletest1@gmail.com")
                .password("profiletest1")
                .build();
        mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request1)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Optional<AccountEntity> expected1 = accountRepository.findByEmail(request1.getEmail());
        assertThat(expected1.isPresent()).isTrue();

        AccountDTO.Registration request2 = AccountDTO.Registration.builder()
                .username("profiletest2")
                .email("profiletest2@gmail.com")
                .password("profiletest2")
                .build();
        mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request2)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Optional<AccountEntity> expected2 = accountRepository.findByEmail(request2.getEmail());
        assertThat(expected2.isPresent()).isTrue();

        // login
        AccountDTO.Auth auth1 = AccountDTO.Auth.builder()
                .email("profiletest1@gmail.com")
                .password("profiletest1")
                .build();
        ResultActions perform1 = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(auth1)))
                        .accept(MediaType.APPLICATION_JSON));
        AccountDTO accountDTO1 = objectMapper.readValue(perform1.andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), AccountDTO.class);
        Bearer1 = accountDTO1.getToken();
        assertThat(Bearer1).isNotEmpty();

        AccountDTO.Auth auth2 = AccountDTO.Auth.builder()
                .email("profiletest2@gmail.com")
                .password("profiletest2")
                .build();
        ResultActions perform2 = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Objects.requireNonNull(objectMapper.writeValueAsString(auth2)))
                        .accept(MediaType.APPLICATION_JSON));
        AccountDTO accountDTO2 = objectMapper.readValue(perform2.andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), AccountDTO.class);
        Bearer2 = accountDTO2.getToken();
        assertThat(Bearer2).isNotEmpty();
    }

    @Test
    @Order(2)
    void itShouldGetProfile() throws Exception {
        // given
        // when
        mockMvc.perform(get("/api/profiles/profiletest1")
                                .header(HttpHeaders.AUTHORIZATION, "Token " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile.username", Matchers.is("profiletest1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile.following", Matchers.is(false)));
    }

    @Test
    @Order(2)
    void itShouldNotGetProfile_whenProfileIsNotFound() throws Exception {
        // given
        // when
        mockMvc.perform(get("/api/profiles/profiletest3")
                                .header(HttpHeaders.AUTHORIZATION, "Token " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    void itShouldFollowProfile() throws Exception {
        // given
        // when
        mockMvc.perform(post("/api/profiles/profiletest2/follow")
                                .header(HttpHeaders.AUTHORIZATION, "Token " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile.username", Matchers.is("profiletest2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile.following", Matchers.is(true)));
    }

    @Test
    @Order(3)
    void itShouldNotFollowProfile_whenProfileIsNotFound() throws Exception {
        // given
        // when
        mockMvc.perform(post("/api/profiles/profiletest3/follow")
                                .header(HttpHeaders.AUTHORIZATION, "Token " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    void itShouldGetFollowings() throws Exception {
        // given
        // when
        mockMvc.perform(get("/api/profiles/followings")
                                .header(HttpHeaders.AUTHORIZATION, "Token " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].profile.username", Matchers.is("profiletest2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].profile.following", Matchers.is(true)));
    }

    @Test
    @Order(5)
    void itShouldGetFollowers() throws Exception {
        // given
        // when
        mockMvc.perform(get("/api/profiles/followers")
                                .header(HttpHeaders.AUTHORIZATION, "Token " + Bearer2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].profile.username", Matchers.is("profiletest1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].profile.following", Matchers.is(false)));
    }

    @Test
    @Order(6)
    void itShouldUnfollowProfile() throws Exception {
        // given
        // when
        mockMvc.perform(delete("/api/profiles/profiletest2/follow")
                                .header(HttpHeaders.AUTHORIZATION, "Token " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile.username", Matchers.is("profiletest2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile.following", Matchers.is(false)));
    }
}