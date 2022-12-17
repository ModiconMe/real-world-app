package edu.popov.domain.article.controller;

import edu.popov.domain.article.dto.ArticleDTO;
import edu.popov.domain.article.dto.CommentDTO;
import edu.popov.domain.article.model.ArticleFilter;
import edu.popov.domain.article.model.FeedParams;
import edu.popov.domain.article.service.ArticleService;
import edu.popov.domain.article.service.CommentService;
import edu.popov.security.AccountDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/articles")
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
    public ArticleDTO createArticle(@RequestBody ArticleDTO articleDTO, @AuthenticationPrincipal AccountDetails accountDetails) {
        return articleService.createArticle(articleDTO, accountDetails.id());
    }

    @GetMapping("/{slug}")
    public ArticleDTO getArticleSlug(@PathVariable("slug") String slug, @AuthenticationPrincipal AccountDetails accountDetails) {
        return articleService.getArticleBySlug(slug, accountDetails.id());
    }

    @PutMapping("/{slug}")
    public ArticleDTO updateArticle(
            @PathVariable("slug") String slug,
            @RequestBody ArticleDTO.Update update, @AuthenticationPrincipal AccountDetails accountDetails) {
        return articleService.updateArticle(slug, update, accountDetails.id());
    }

    @DeleteMapping("/{slug}")
    public Long deleteArticle(
            @PathVariable("slug") String slug,
            @AuthenticationPrincipal AccountDetails accountDetails) {
        return articleService.deleteArticle(slug, accountDetails.getUsername());
    }

    @PostMapping("/{slug}/favorite")
    public ArticleDTO favoriteArticle(
            @PathVariable("slug") String slug,
            @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        return articleService.favoriteArticle(slug, accountDetails.id());
    }

    @DeleteMapping("/{slug}/favorite")
    public ArticleDTO unfavoriteArticle(
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
        return articleService.getArticlesByFeed(feedParams, accountDetails.id());
    }

    @PostMapping("/{slug}/comments")
    public CommentDTO addComment(
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

    @DeleteMapping("/{slug}/comments/{id}")
    public CommentDTO deleteComment(
            @PathVariable("slug") String slug,
            @PathVariable("id") Long commentId,
            @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        return commentService.deleteComment(slug, commentId);
    }


}