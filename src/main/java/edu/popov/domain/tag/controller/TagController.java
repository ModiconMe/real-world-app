package edu.popov.domain.tag.controller;

import edu.popov.domain.tag.dto.TagDTO;
import edu.popov.domain.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/tags")
public class TagController {

    private TagService tagService;

    @GetMapping
    public TagDTO.TagList getTags() {
        return TagDTO.TagList.builder().tags(tagService.getTags()).build();
    }
}