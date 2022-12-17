package edu.popov.domain.article.repository;

import edu.popov.domain.article.entity.FavoriteEntityId;
import edu.popov.domain.article.entity.FavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<FavoriteEntity, FavoriteEntityId> {
}
