package edu.popov.domain.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.account.repository.AccountRepository;
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

    private static String Bearer;

    @Test
    @Order(1)
    void itShouldRegisterAccount() throws Exception {
        accountRepository.deleteAll();
        // given
        AccountDTO.Registration request = AccountDTO.Registration.builder()
                .username("user1")
                .email("user1@gmail.com")
                .password("pass1")
                .build();

        // when
        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        // then
        Optional<AccountEntity> expected = accountRepository.findByEmail(request.getEmail());
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
        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        // then
        Optional<AccountEntity> expected = accountRepository.findByEmail(request.getEmail());
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
        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        // then
        Optional<AccountEntity> expected = accountRepository.findByUsername(request.getUsername());
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
        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        // then
        Optional<AccountEntity> expected = accountRepository.findByUsername(request.getEmail());
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
                        post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request)))
                                .accept(MediaType.APPLICATION_JSON));

        // then
        AccountDTO accountDTO = objectMapper.readValue(perform.andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), AccountDTO.class);
        Bearer = accountDTO.getToken();
        assertThat(Bearer).isNotEmpty();
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
        mockMvc.perform(
                        post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

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
        mockMvc.perform(
                        post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

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
        mockMvc.perform(put("/api/user")
                                .header(HttpHeaders.AUTHORIZATION, "Token " + Bearer)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.email", Matchers.is(request.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.username", Matchers.is(request.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.bio", Matchers.is(request.getBio())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.image", Matchers.is(request.getImage())));

        // then
        Optional<AccountEntity> optionalAccount = accountRepository.findByEmail(request.getEmail());
        assertThat(optionalAccount.isPresent());
        assertThat(optionalAccount.get().getBio()).isEqualTo(request.getBio());
    }

    @Test
    @Order(9)
    void itShouldNotUpdateAccount_whenEmailIsAlreadyExists() throws Exception {
        // given
        AccountDTO.Update request = AccountDTO.Update.builder()
                .username("user1")
                .email("user2@gmail.com")
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

        AccountDTO.Registration registration = AccountDTO.Registration.builder()
                .username("user2")
                .email("user2@gmail.com")
                .password("pass2")
                .build();

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(Objects.requireNonNull(objectMapper.writeValueAsString(registration)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // when
        ResultActions perform = mockMvc
                .perform(
                        put("/api/user")
                                .header(HttpHeaders.AUTHORIZATION, "Token " + Bearer)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        // then
    }

    @Test
    @Order(10)
    void itShouldNotUpdateAccount_whenUsernameIsAlreadyExists() throws Exception {
        // given
        AccountDTO.Update request = AccountDTO.Update.builder()
                .username("user2")
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
                        put("/api/user")
                                .header(HttpHeaders.AUTHORIZATION, "Token " + Bearer)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        // then
    }

}