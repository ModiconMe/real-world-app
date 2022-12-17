package edu.popov.domain.article.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.popov.domain.account.dto.AccountDTO;
import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.account.repository.AccountRepository;
import edu.popov.domain.article.dto.ArticleDTO;
import edu.popov.domain.article.dto.CommentDTO;
import edu.popov.domain.article.repository.ArticleRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ArticleRepository articleRepository;

    private static String Bearer1;
    private static String Bearer2;

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
        Optional<AccountEntity> expected1 = accountRepository.findByEmail(request1.getEmail());
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
        Optional<AccountEntity> expected2 = accountRepository.findByEmail(request2.getEmail());
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
    void itShouldCreateArticle() throws Exception {
        // given
        ArticleDTO articleDTO = ArticleDTO.builder()
                .slug("title")
                .title("title")
                .description("desc")
                .body("body")
                .tags(List.of("tag1", "tag2"))
                .build();

        String json = objectMapper.writeValueAsString(articleDTO);

        // when send invalid request get error
        mockMvc
                .perform(
                        post("/api/v1/articles")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("random string")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // create an article
        mockMvc
                .perform(
                        post("/api/v1/articles")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.slug", Matchers.is("title")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("title")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is("desc")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body", Matchers.is("body")));

        // read an article by slug
        mockMvc
                .perform(
                        get("/api/v1/articles/title")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.slug", Matchers.is("title")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("title")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is("desc")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body", Matchers.is("body")));

        // read not existing article
        mockMvc
                .perform(
                        get("/api/v1/articles/not_exist")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // update an article
        ArticleDTO.Update newArticleDTO = ArticleDTO.Update.builder()
                .title("title1")
                .description("desc1")
                .body("body")
                .build();

        String newJson = objectMapper.writeValueAsString(newArticleDTO);

        mockMvc
                .perform(
                        put("/api/v1/articles/title")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(newJson)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.slug", Matchers.is("title1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("title1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is("desc1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body", Matchers.is("body")));

        // update not existing article
        mockMvc
                .perform(
                        put("/api/v1/articles/not_exist")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(newJson)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // favor article
        mockMvc
                .perform(
                        post("/api/v1/articles/title1/favorite")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.favorited", Matchers.is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.favoritesCount", Matchers.is(1)));
        mockMvc
                .perform(
                        post("/api/v1/articles/title1/favorite")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.favorited", Matchers.is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.favoritesCount", Matchers.is(2)));

        // unfavor article
        mockMvc
                .perform(
                        delete("/api/v1/articles/title1/favorite")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.favorited", Matchers.is(false)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.favoritesCount", Matchers.is(1)));

        // delete an article
        mockMvc
                .perform(
                        delete("/api/v1/articles/title1")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(newJson)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // try to read after deleting
        mockMvc
                .perform(
                        get("/api/v1/articles/title1")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    void itShouldReadAnArticles_whenNotAuthenticate() throws Exception {
        // given
        ArticleDTO articleDTO1 = ArticleDTO.builder()
                .slug("title1")
                .title("title1")
                .description("desc1")
                .body("body1")
                .tags(List.of("tag1", "tag2"))
                .build();
        ArticleDTO articleDTO2 = ArticleDTO.builder()
                .slug("title2")
                .title("title2")
                .description("desc2")
                .body("body2")
                .tags(List.of("tag1", "tag2"))
                .build();
        ArticleDTO articleDTO3 = ArticleDTO.builder()
                .slug("title3")
                .title("title3")
                .description("desc3")
                .body("body3")
                .tags(List.of("tag1", "tag2"))
                .build();
        ArticleDTO articleDTO4 = ArticleDTO.builder()
                .slug("title4")
                .title("title4")
                .description("desc4")
                .body("body4")
                .tags(List.of("tag3", "tag4"))
                .build();

        String json1 = objectMapper.writeValueAsString(articleDTO1);
        String json2 = objectMapper.writeValueAsString(articleDTO2);
        String json3 = objectMapper.writeValueAsString(articleDTO3);
        String json4 = objectMapper.writeValueAsString(articleDTO4);

        // save some articles
        mockMvc
                .perform(
                        post("/api/v1/articles")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json1)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc
                .perform(
                        post("/api/v1/articles")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json2)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc
                .perform(
                        post("/api/v1/articles")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json3)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc
                .perform(
                        post("/api/v1/articles")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json4)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // when

        mockMvc
                .perform(
                        post("/api/v1/articles/title1/favorite")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.favorited", Matchers.is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.favoritesCount", Matchers.is(1)));

        String contentAsString = mockMvc
                .perform(
                        get("/api/v1/articles")
                                .param("limit", "2")
                                .param("offset", "0")
                                .param("favorited", "user2")
                                .param("tag", "tag1")
                                .param("author", "user1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        ArticleDTO.MultipleArticle multipleArticle = objectMapper.readValue(contentAsString, ArticleDTO.MultipleArticle.class);
        assertThat(multipleArticle.getArticles().size()).isEqualTo(1);
    }

    @Test
    @Order(4)
    void itShouldGetFeed_whenAuthenticate() throws Exception {
        // follow user2
        mockMvc
                .perform(
                        post("/api/v1/profiles/user2/follow")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // when
        String contentAsString = mockMvc
                .perform(
                        get("/api/v1/articles/feed")
                                .param("limit", "2")
                                .param("offset", "0")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        ArticleDTO.MultipleArticle multipleArticle = objectMapper.readValue(contentAsString, ArticleDTO.MultipleArticle.class);
        assertThat(multipleArticle.getArticles().size()).isEqualTo(2);
    }

    @Test
    @Order(5)
    void itShouldAddCommentToArticle() throws Exception {
        // given
        CommentDTO.Create commentDTO1 = CommentDTO.Create.builder()
                .body("comment1")
                .build();

        CommentDTO.Create commentDTO2 = CommentDTO.Create.builder()
                .body("comment2")
                .build();

        CommentDTO.Create commentDTO3 = CommentDTO.Create.builder()
                .body("comment3")
                .build();

        CommentDTO.Create commentDTO4 = CommentDTO.Create.builder()
                .body("comment4")
                .build();

        String json1 = objectMapper.writeValueAsString(commentDTO1);
        String json2 = objectMapper.writeValueAsString(commentDTO2);
        String json3 = objectMapper.writeValueAsString(commentDTO3);
        String json4 = objectMapper.writeValueAsString(commentDTO4);

        // try to add comment without login and get UNAUTHORIZED exception
        mockMvc
                .perform(
                        post("/api/v1/articles/title1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json1)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        // add some comments
        mockMvc
                .perform(
                        post("/api/v1/articles/title1/comments")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json1)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.body", Matchers.is("comment1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author.username", Matchers.is("user1")));
        mockMvc
                .perform(
                        post("/api/v1/articles/title1/comments")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json2)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.body", Matchers.is("comment2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author.username", Matchers.is("user1")));
        mockMvc
                .perform(
                        post("/api/v1/articles/title2/comments")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json3)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.body", Matchers.is("comment3")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author.username", Matchers.is("user1")));
        mockMvc
                .perform(
                        post("/api/v1/articles/title3/comments")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json4)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.body", Matchers.is("comment4")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author.username", Matchers.is("user1")));

        // get comments by slug with no login
        String comments1 = mockMvc
                .perform(
                        get("/api/v1/articles/title1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        CommentDTO.MultipleComments article1Comments = objectMapper.readValue(comments1, CommentDTO.MultipleComments.class);
        assertThat(article1Comments.getComments().size()).isEqualTo(2);
        String comments2 = mockMvc
                .perform(
                        get("/api/v1/articles/title2/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        CommentDTO.MultipleComments article2Comments = objectMapper.readValue(comments2, CommentDTO.MultipleComments.class);
        assertThat(article2Comments.getComments().size()).isEqualTo(1);

        // try delete comment without login and get FORBIDDEN
        mockMvc
                .perform(
                        delete("/api/v1/articles/title1/comments/2")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        // try delete comment other account comment and get UNAUTHORIZED
        mockMvc
                .perform(
                        delete("/api/v1/articles/title1/comments/2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        // delete our comments
        mockMvc
                .perform(
                        delete("/api/v1/articles/title1/comments/2")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + Bearer1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.body", Matchers.is("comment2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.author.username", Matchers.is("user1")));

        String commentsAfterDeleting = mockMvc
                .perform(
                        get("/api/v1/articles/title1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        CommentDTO.MultipleComments answerCommentsAfterDeleting = objectMapper.readValue(commentsAfterDeleting, CommentDTO.MultipleComments.class);
        assertThat(answerCommentsAfterDeleting.getComments().size()).isEqualTo(1);
    }
}