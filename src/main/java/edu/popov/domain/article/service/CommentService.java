package edu.popov.domain.article.service;

import edu.popov.domain.article.dto.CommentDTO;

public interface CommentService {

    CommentDTO.SingleComment addComment(String slug, CommentDTO.Create comment, Long id);

    CommentDTO.MultipleComments getComments(String slug);

    void deleteComment(String slug, Long commentId, Long userId);

}
