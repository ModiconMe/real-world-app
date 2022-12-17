package edu.popov.domain.article.service;

import edu.popov.domain.article.dto.CommentDTO;

public interface CommentService {

    CommentDTO addComment(String slug, CommentDTO.Create comment, Long id);

    CommentDTO.MultipleComments getComments(String slug);

    CommentDTO deleteComment(String slug, Long commentId);

}
