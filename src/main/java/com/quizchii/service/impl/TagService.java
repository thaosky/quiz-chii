package com.quizchii.service.impl;

import com.quizchii.Enum.SortDir;
import com.quizchii.entity.QuestionTagEntity;
import com.quizchii.entity.TagEntity;
import com.quizchii.entity.TestTagEntity;
import com.quizchii.model.ListResponse;
import com.quizchii.repository.QuestionTagRepository;
import com.quizchii.repository.TagRepository;
import com.quizchii.repository.TestTagRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TagService {

    private TagRepository tagRepository;
    private TestTagRepository testTagRepository;
    private QuestionTagRepository questionTagRepository;

    public TagEntity create(TagEntity tag) {
        return tagRepository.save(tag);
    }

    public void delete(Long id) {
         tagRepository.deleteById(id);

        // Xoa tag o bai kiem tra
        List<TestTagEntity> list = testTagRepository.findAllByTagId(id);
        testTagRepository.deleteAll(list);

        // Xoa tag o cau hoi
        List<QuestionTagEntity> list1 = questionTagRepository.findAllByTagId(id);
        questionTagRepository.deleteAll(list1);
    }
    public TagEntity getTagById(Long id) {
        return tagRepository.findById(id).get();
    }
    public TagEntity update(TagEntity tag, Long id) {
        Optional<TagEntity> optional = tagRepository.findById(id);
        TagEntity tag1 = optional.get();
        BeanUtils.copyProperties(tag, tag1, "id");
        return tagRepository.save(tag1);
    }

    public ListResponse<TagEntity> getAllTag(Integer pageSize, Integer pageNo, String sortName, String sortDir, String name) {
        if ("".equals(name)) {
            name = null;
        }
        Sort sortable = Sort.by("id").descending();
        if (sortName != null && SortDir.ASC.getValue().equals(sortDir)) {
            sortable = Sort.by(sortName).ascending();
        } else if (sortName != null && SortDir.DESC.getValue().equals(sortDir)) {
            sortable = Sort.by(sortName).descending();
        }
        Pageable pageable =  PageRequest.of(pageNo, pageSize, sortable);
        Page<TagEntity> page = tagRepository.findAllByName(name, pageable);
        ListResponse<TagEntity> response = new ListResponse();
        response.setItems(page.toList());
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotalElements((int) page.getTotalElements());
        response.setTotalPage((int) page.getTotalPages());
        return response;
    }
}
