package edu.popov.domain.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.entity.Account;
import edu.popov.domain.account.repository.AccountRepository;
import edu.popov.security.AccountDetails;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    private AccountController underTest;

    private static String Bearer;

    @Test
    @Order(1)
    void itShouldRegisterAccount() throws Exception {
        // given
        AccountDTO.Registration request = AccountDTO.Registration.builder()
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .build();

        // when
        ResultActions perform = mockMvc
                .perform(
                        post("/api/v1/users/registry")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))
                                )
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        // then
        Optional<Account> expected = accountRepository.findByEmail(request.getEmail());
        assertThat(expected.isPresent()).isTrue();
    }

    @Test
    @Order(2)
    void itShouldNotRegisterAccount_whenUsernameIsAlreadyExist() throws Exception {
        // given
        AccountDTO.Registration request = AccountDTO.Registration.builder()
                .username("user1")
                .email("user2@gmail.com")
                .password("pass1")
                .build();

        // when
        ResultActions perform = mockMvc
                .perform(
                        post("/api/v1/users/registry")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))
                                )
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        // then
        Optional<Account> expected = accountRepository.findByEmail(request.getEmail());
        assertThat(expected.isPresent()).isFalse();
    }

    @Test
    @Order(3)
    void itShouldNotRegisterAccount_whenEmailIsAlreadyExist() throws Exception {
        // given
        AccountDTO.Registration request = AccountDTO.Registration.builder()
                .username("user2")
                .email("user1@gmail.com")
                .password("pass1")
                .build();

        // when
        ResultActions perform = mockMvc
                .perform(
                        post("/api/v1/users/registry")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))
                                )
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        // then
        Optional<Account> expected = accountRepository.findByUsername(request.getUsername());
        assertThat(expected.isPresent()).isFalse();
    }

    @Test
    @Order(4)
    void itShouldNotRegisterAccount_whenRequiredFieldIsEmpty() throws Exception {
        // given
        AccountDTO.Registration request = AccountDTO.Registration.builder()
                .email("user3@gmail.com")
                .password("pass1")
                .build();

        // when
        ResultActions perform = mockMvc
                .perform(
                        post("/api/v1/users/registry")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))
                                )
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        // then
        Optional<Account> expected = accountRepository.findByUsername(request.getEmail());
        assertThat(expected.isPresent()).isFalse();
    }

    @Test
    @Order(5)
    void itShouldAuthAccount() throws Exception {
        // given
        AccountDTO.Auth request = AccountDTO.Auth.builder()
                .email("user1@gmail.com")
                .password("pass1")
                .build();

        // when
        ResultActions perform = mockMvc
                .perform(
                        post("/api/v1/users/auth")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))
                                )
                                .accept(MediaType.APPLICATION_JSON));

        // then
        Bearer = perform.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }

    @Test
    @Order(6)
    void itShouldNotAuthAccount_whenEmailIsNotExist() throws Exception {
        // given
        AccountDTO.Auth request = AccountDTO.Auth.builder()
                .email("user3@gmail.com")
                .password("pass1")
                .build();

        // when
        ResultActions perform = mockMvc
                .perform(
                        post("/api/v1/users/auth")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))
                                )
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        // then
    }

    @Test
    @Order(7)
    void itShouldNotAuthAccount_whenPasswordIsWrong() throws Exception {
        // given
        AccountDTO.Auth request = AccountDTO.Auth.builder()
                .email("user1@gmail.com")
                .password("pass2")
                .build();

        // when
        ResultActions perform = mockMvc
                .perform(
                        post("/api/v1/users/auth")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))
                                )
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        // then
    }

    @Test
    @Order(8)
    void itShouldUpdateAccount() throws Exception {
        // given
        AccountDTO.Update request = AccountDTO.Update.builder()
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .bio("bio")
                .image("image")
                .build();

        AccountDetails accountDetails = AccountDetails.builder()
                .email("user1@gmail.com")
                .password("pass1")
                .id(1L)
                .build();

        String json = objectMapper.writeValueAsString(request) + "; " + objectMapper.writeValueAsString(accountDetails);
        // when
        ResultActions perform = mockMvc
                .perform(
                        put("/api/v1/users")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json
                                )
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(request.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is(request.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.bio", Matchers.is(request.getBio())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.image", Matchers.is(request.getImage())));

        // then
        Optional<Account> optionalAccount = accountRepository.findByEmail(request.getEmail());
        assertThat(optionalAccount.isPresent());
        assertThat(optionalAccount.get().getBio()).isEqualTo(request.getBio());
    }
}