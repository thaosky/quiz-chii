package com.quizchii.service;

import com.quizchii.Enum.SortDir;
import com.quizchii.common.StatusCode;
import com.quizchii.entity.QuestionEntity;
import com.quizchii.entity.QuestionTagEntity;
import com.quizchii.entity.TagEntity;
import com.quizchii.common.BusinessException;
import com.quizchii.model.ListResponse;
import com.quizchii.model.QuestionRequest;
import com.quizchii.model.QuestionResponse;
import com.quizchii.repository.QuestionRepository;
import com.quizchii.repository.QuestionTagRepository;
import com.quizchii.repository.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class QuestionService {
    private QuestionRepository questionRepository;
    private TagRepository tagRepository;
    private QuestionTagRepository questionTagRepository;

    public ListResponse getAll(Integer pageSize, Integer pageNo, String sortName, String sortDir, String content, Long tagId) {
        // Paging & sorting
        if ("".equals(content)) {
            content = null;
        }
        Sort sortable = Sort.by("id").descending();
        if (sortName != null && SortDir.ASC.getValue().equals(sortDir)) {
            sortable = Sort.by(sortName).ascending();
        } else if (sortName != null && SortDir.DESC.getValue().equals(sortDir)) {
            sortable = Sort.by(sortName).descending();
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortable);
        Page<QuestionEntity> page = questionRepository.listQuestion(content, tagId, pageable);

        ListResponse<QuestionResponse> response = new ListResponse();
        List<QuestionEntity> entities = page.toList();
        List<QuestionResponse> questionResponseList = new ArrayList<>();
        for (QuestionEntity entity : entities) {
            QuestionResponse item = new QuestionResponse();
            BeanUtils.copyProperties(entity, item);
            List<TagEntity> tagEntityList = tagRepository.findAllByQuestionId(entity.getId());
            item.setTagList(tagEntityList);
            questionResponseList.add(item);
        }
        response.setItems(questionResponseList);
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotalElements((int) page.getTotalElements());
        response.setTotalPage((int) page.getTotalPages());

        return response;
    }

    public QuestionRequest create(QuestionRequest request) {
        QuestionEntity questionEntity = new QuestionEntity();
        BeanUtils.copyProperties(request, questionEntity);
        QuestionEntity questionSaved = questionRepository.save(questionEntity);
        List<TagEntity> tagList = request.getTagList();
        for (TagEntity tag : tagList) {
            QuestionTagEntity questionTagEntity = new QuestionTagEntity();
            questionTagEntity.setQuestionId(questionSaved.getId());
            questionTagEntity.setTagId(tag.getId());
            questionTagRepository.save(questionTagEntity);
        }
        request.setId(questionSaved.getId());
        return request;
    }

    public QuestionResponse getQuestionById(Long questionId) {
        Optional<QuestionEntity> optional = questionRepository.findById(questionId);
        QuestionEntity questionEntity = optional.orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, StatusCode.TAG_NOT_FOUND));

        QuestionResponse response = new QuestionResponse();
        BeanUtils.copyProperties(questionEntity, response);
        List<TagEntity> tagEntityList = tagRepository.findAllByQuestionId(questionId);
        response.setTagList(tagEntityList);
        return response;
    }

    private QuestionResponse getQuestionByQuestionEntity(QuestionEntity questionEntity) {
        QuestionResponse response = new QuestionResponse();
        BeanUtils.copyProperties(questionEntity, response);
        List<TagEntity> tagEntityList = tagRepository.findAllByQuestionId(questionEntity.getId());
        response.setTagList(tagEntityList);
        return response;
    }

    public QuestionResponse addTagByQuestion(Long questionId, List<TagEntity> tagEntityList) {
        // Add tag
        for (TagEntity tag : tagEntityList) {
            QuestionTagEntity questionTagEntity = new QuestionTagEntity();
            questionTagEntity.setQuestionId(questionId);
            questionTagEntity.setTagId(tag.getId());
            questionTagRepository.save(questionTagEntity);
        }
        return getQuestionById(questionId);
    }

    public QuestionResponse deleteTagByQuestion(Long questionId, Long tagId) {
        Optional<QuestionTagEntity> optional = questionTagRepository.findByQuestionIdAndTagId(questionId, tagId);
        QuestionTagEntity questionTagEntity = optional.orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, StatusCode.TAG_NOT_FOUND));
        questionTagRepository.delete(questionTagEntity);

        return getQuestionById(questionId);
    }

    @Transactional
    public void delete(Long id) {
        Optional<QuestionEntity> optional = questionRepository.findById(id);
        QuestionEntity questionEntity = optional.orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, StatusCode.TAG_NOT_FOUND));
        questionRepository.delete(questionEntity);

        List<QuestionTagEntity> list = questionTagRepository.findAllByQuestionId(id);
        questionTagRepository.deleteAll(list);
    }

    public QuestionRequest update(QuestionRequest request, Long id) {
        QuestionEntity questionEntity = questionRepository.findById(id).get();
        BeanUtils.copyProperties(request, questionEntity);
        questionRepository.save(questionEntity);

        List<QuestionTagEntity> list = questionTagRepository.findAllByQuestionId(id);
        questionTagRepository.deleteAll(list);

        List<TagEntity> tagList = request.getTagList();
        for (TagEntity tag : tagList) {
            QuestionTagEntity questionTagEntity = new QuestionTagEntity();
            questionTagEntity.setQuestionId(questionEntity.getId());
            questionTagEntity.setTagId(tag.getId());
            questionTagRepository.save(questionTagEntity);
        }
        return request;
    }
}
// xin chào hợp béo ú
// tôi là thảo đây
///ádbasnfbnasfd
/// adadaksdjas
