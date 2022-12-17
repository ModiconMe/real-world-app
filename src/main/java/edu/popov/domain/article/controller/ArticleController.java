package edu.popov.domain.article.controller;

import edu.popov.domain.article.dto.ArticleDTO;
import edu.popov.domain.article.dto.CommentDTO;
import edu.popov.domain.article.model.ArticleFilter;
import edu.popov.domain.article.model.FeedParams;
import edu.popov.domain.article.service.ArticleService;
import edu.popov.domain.article.service.CommentService;
import edu.popov.security.AccountDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final CommentService commentService;

    @GetMapping
    public ArticleDTO.MultipleArticle getArticlesByFilter(
            @ModelAttribute ArticleFilter articleFilter,
            @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        return articleService.getArticlesByFilter(articleFilter, accountDetails);
    }

    @PostMapping
    public ArticleDTO.SingleArticle<ArticleDTO> createArticle(
            @RequestBody ArticleDTO.SingleArticle<ArticleDTO> articleDTO,
            @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        return articleService.createArticle(articleDTO.getArticle(), accountDetails.id());
    }

    @GetMapping("/{slug}")
    public ArticleDTO.SingleArticle<ArticleDTO> getArticleSlug(
            @PathVariable("slug") String slug,
            @AuthenticationPrincipal AccountDetails accountDetails) {

        Long id = null;
        if (!Objects.isNull(accountDetails))
            id = accountDetails.id();

        return articleService.getArticleBySlug(slug, id);
    }

    @PutMapping("/{slug}")
    public ArticleDTO.SingleArticle<ArticleDTO> updateArticle(
            @PathVariable("slug") String slug,
            @RequestBody ArticleDTO.SingleArticle<ArticleDTO.Update> update,
            @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        return articleService.updateArticle(slug, update.getArticle(), accountDetails.id());
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{slug}")
    public void deleteArticle(
            @PathVariable("slug") String slug,
            @AuthenticationPrincipal AccountDetails accountDetails) {
        articleService.deleteArticle(slug, accountDetails.getUsername());
    }

    @PostMapping("/{slug}/favorite")
    public ArticleDTO.SingleArticle<ArticleDTO> favoriteArticle(
            @PathVariable("slug") String slug,
            @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        return articleService.favoriteArticle(slug, accountDetails.id());
    }

    @DeleteMapping("/{slug}/favorite")
    public ArticleDTO.SingleArticle<ArticleDTO> unfavoriteArticle(
            @PathVariable("slug") String slug,
            @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        return articleService.unfavoriteArticle(slug, accountDetails.id());
    }

    @GetMapping("/feed")
    public ArticleDTO.MultipleArticle getArticlesByFeed(
            @ModelAttribute FeedParams feedParams,
            @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        Long id = null;
        if (!Objects.isNull(accountDetails))
            id = accountDetails.id();

        return articleService.getArticlesByFeed(feedParams, id);
    }

    @PostMapping("/{slug}/comments")
    public CommentDTO.SingleComment addComment(
            @PathVariable("slug") String slug,
            @RequestBody CommentDTO.Create comment,
            @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        return commentService.addComment(slug, comment, accountDetails.id());
    }

    @GetMapping("/{slug}/comments")
    public CommentDTO.MultipleComments getComment(
            @PathVariable("slug") String slug
    ) {
        return commentService.getComments(slug);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{slug}/comments/{id}")
    public void deleteComment(
            @PathVariable("slug") String slug,
            @PathVariable("id") Long commentId,
            @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        commentService.deleteComment(slug, commentId, accountDetails.id());
    }

}