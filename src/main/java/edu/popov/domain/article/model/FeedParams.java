package edu.popov.domain.article.model;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedParams {

    private Integer limit;
    private Integer offset;

}