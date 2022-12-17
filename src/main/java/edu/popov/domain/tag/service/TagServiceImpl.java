package edu.popov.domain.tag.service;

import edu.popov.domain.tag.entity.TagEntity;
import edu.popov.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<String> getTags() {
        return tagRepository.findAll().stream()
                .map(TagEntity::getTagName)
                .distinct()
                .toList();
    }
}
