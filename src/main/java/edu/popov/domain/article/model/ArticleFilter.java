package edu.popov.domain.article.model;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleFilter {

    private String tag;
    private String author;
    private String favorited;
    private Integer limit;
    private Integer offset;

}