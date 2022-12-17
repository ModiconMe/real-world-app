package edu.popov.domain.article.service;

import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.article.dto.CommentDTO;
import edu.popov.domain.article.dto.CommentMapper;
import edu.popov.domain.article.entity.ArticleEntity;
import edu.popov.domain.article.entity.CommentEntity;
import edu.popov.domain.article.repository.ArticleRepository;
import edu.popov.domain.article.repository.CommentRepository;
import edu.popov.domain.profile.service.ProfileService;
import edu.popov.utils.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static java.lang.String.format;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ProfileService profileService;
    private final ArticleRepository articleRepository;
    private final CommentMapper commentMapper;

    private static final String ARTICLE_NOT_FOUND_BY_SLUG = "Article with slug %s is not found";
    private static final String COMMENT_NOT_FOUND_BY_SLUG_AND_ID = "Comment with id %d of article with slug %s is not found";

    @Override
    @Transactional
    public CommentDTO addComment(String slug, CommentDTO.Create comment, Long id) {
        ArticleEntity article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException(format(ARTICLE_NOT_FOUND_BY_SLUG, slug)));

        AccountEntity account = profileService.getAccountById(id);

        CommentEntity commentEntity = CommentEntity.builder()
                .body(comment.getBody())
                .account(account)
                .article(article)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CommentEntity savedComment = commentRepository.save(commentEntity);

        return commentMapper.mapToCommentDTO(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDTO.MultipleComments getComments(String slug) {
        ArticleEntity article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException(format(ARTICLE_NOT_FOUND_BY_SLUG, slug)));

        return CommentDTO.MultipleComments.builder()
                .comments(
                        commentMapper.mapToCommentDTOList(
                                commentRepository.findByArticle(article))
                )
                .build();
    }

    @Override
    @Transactional
    public CommentDTO deleteComment(String slug, Long commentId) {
        ArticleEntity article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException(format(ARTICLE_NOT_FOUND_BY_SLUG, slug)));

        CommentEntity commentEntity = commentRepository.findByIdAndArticle(commentId, article)
                .orElseThrow(() -> new NotFoundException(format(COMMENT_NOT_FOUND_BY_SLUG_AND_ID, commentId, slug)));

        commentRepository.deleteById(commentId);

        return commentMapper.mapToCommentDTO(commentEntity);
    }
}
