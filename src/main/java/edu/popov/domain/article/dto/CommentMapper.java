package edu.popov.domain.article.dto;

import edu.popov.domain.account.dto.AccountMapper;
import edu.popov.domain.article.entity.ArticleEntity;
import edu.popov.domain.article.entity.CommentEntity;
import edu.popov.domain.article.entity.FavoriteEntity;
import edu.popov.domain.profile.dto.ProfileMapper;
import edu.popov.domain.profile.service.ProfileService;
import edu.popov.domain.tag.entity.TagEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentMapper {

    private final ProfileMapper profileMapper;
    private final ProfileService profileService;
    private final AccountMapper accountMapper;

    public CommentDTO mapToCommentDTO(CommentEntity commentEntity) {
        return CommentDTO.builder()
                .id(commentEntity.getId())
                .body(commentEntity.getBody())
                .author(accountMapper.mapToAccountDTO(commentEntity.getAccount()))
                .createdAt(commentEntity.getCreatedAt())
                .updatedAt(commentEntity.getUpdatedAt())
                .build();
    }

    public List<CommentDTO> mapToCommentDTOList(List<CommentEntity> commentEntities) {
        return commentEntities.stream().map(this::mapToCommentDTO).toList();
    }
}