package edu.popov.domain.article.repository;

import edu.popov.domain.article.entity.ArticleEntity;
import edu.popov.domain.article.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findByArticle(ArticleEntity article);

    Optional<CommentEntity> findByIdAndArticle(Long id, ArticleEntity article);

}
