package edu.popov.domain.article.repository;

import edu.popov.domain.account.entity.AccountEntity;
import edu.popov.domain.article.entity.ArticleEntity;
import edu.popov.domain.article.entity.FavoriteEntity;
import edu.popov.domain.article.entity.FavoriteEntityId;
import edu.popov.domain.tag.entity.TagEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {

    Optional<ArticleEntity> findBySlug(String slug);

    Long deleteBySlug(String slug);

    @Query("SELECT DISTINCT ar FROM Article ar " +
            "LEFT JOIN ar.tags tag " +
            "LEFT JOIN ar.author ac " +
            "LEFT JOIN ar.favoriteList fav " +
            "WHERE " +
            "(:tag IS NULL OR tag.tagName = :tag) AND " +
            "(:author IS NULL OR ac.username = :author) AND " +
            "(:favorited IS NULL OR fav.account = :favorited)")
    List<ArticleEntity> findByFilter(
            @Param("tag") String tag,
            @Param("author") String author,
            @Param("favorited") AccountEntity user,
            Pageable pageable
    );

    @Query("SELECT DISTINCT ar FROM Article ar " +
            "LEFT JOIN ar.author ac " +
            "LEFT JOIN ac.followers fe " +
            "WHERE " +
            "(fe.userAccount = :follower)")
    List<ArticleEntity> findByFeed(
            @Param("follower") AccountEntity user,
            Pageable pageable
    );

}