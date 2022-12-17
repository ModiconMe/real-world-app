package edu.popov.domain.tag.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class TagDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonRootName("tags")
    public static class TagList {
        private List<String> tags;
    }
}