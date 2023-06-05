package com.quizchii.service;


import com.quizchii.Enum.SortDir;
import com.quizchii.entity.*;
import com.quizchii.model.BusinessException;
import com.quizchii.model.ListResponse;
import com.quizchii.model.question.TestDTO;
import com.quizchii.model.Const;
import com.quizchii.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TestService {
    private TestRepository testRepository;
    private TagRepository tagRepository;
    private TestQuestionRepository testQuestionRepository;
    private TestTagRepository testTagRepository;
    private QuestionRepository questionRepository;

    public ListResponse<TestDTO> getAllTest(Integer pageSize, Integer pageNo, String sortName, String sortDir, String name, Long tagId) {
        // Paging & sorting
        if ("".equals(name)) {
            name = null;
        }
        Sort sortable = Sort.by("id").descending();
        if (sortName != null && SortDir.ASC.getValue().equals(sortDir)) {
            sortable = Sort.by(sortName).ascending();
        } else if (sortName != null && SortDir.DESC.getValue().equals(sortDir)) {
            sortable = Sort.by(sortName).descending();
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortable);
        Page<TestEntity> page = testRepository.listTest(name, tagId, pageable);

        ListResponse<TestDTO> response = new ListResponse();
        List<TestEntity> entities = page.toList();
        List<TestDTO> testDTOList = new ArrayList<>();
        for (TestEntity entity : entities) {
            TestDTO item = new TestDTO();
            BeanUtils.copyProperties(entity, item);
            List<TagEntity> tagEntityList = tagRepository.findAllByTestId(entity.getId());
            item.setTagList(tagEntityList);
            testDTOList.add(item);
        }
        response.setItems(testDTOList);
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotalElements((int) page.getTotalElements());
        response.setTotalPage(page.getTotalPages());

        return response;
    }

    public TestDTO getById(Long testId) {
        List<QuestionEntity> questionEntityList = questionRepository.findAllByTestId(testId);
        List<QuestionEntity> done = new ArrayList<>();
        for(QuestionEntity question: questionEntityList) {
            QuestionEntity doneItem = new QuestionEntity();
            BeanUtils.copyProperties(question, doneItem, "correctAnswer");
            done.add(doneItem);
        }
        List<TagEntity> tagEntityList = tagRepository.findAllByTestId(testId);



        TestEntity testEntity = testRepository.findById(testId).get();
        TestDTO dto = new TestDTO();
        BeanUtils.copyProperties(testEntity, dto);
        dto.setQuestionList(done);
        dto.setTagList(tagEntityList);
        return dto;
    }

    public TestDTO create(TestDTO testDTO) {
        List<QuestionEntity> questionEntityList = testDTO.getQuestionList();
        List<TagEntity> tagEntityList = testDTO.getTagList();

        TestEntity testEntity = new TestEntity();
        BeanUtils.copyProperties(testDTO, testEntity);
        TestEntity save = testRepository.save(testEntity);
        // save test_question
        for (QuestionEntity questionEntity : questionEntityList) {
            TestQuestionEntity testQuestionEntity = new TestQuestionEntity();
            testQuestionEntity.setTestId(save.getId());
            testQuestionEntity.setQuestionId(questionEntity.getId());

            testQuestionRepository.save(testQuestionEntity);
        }

        // save test_tag
        for (TagEntity tag : tagEntityList) {
            TestTagEntity testTagEntity = new TestTagEntity();
            testTagEntity.setTestId(save.getId());
            testTagEntity.setTagId(tag.getId());

            testTagRepository.save(testTagEntity);
        }

        testDTO.setId(save.getId());
        return testDTO;
    }

    public TestDTO update(TestDTO request, Long id) {
        TestEntity testEntity = testRepository.findById(id).get();
        BeanUtils.copyProperties(request, testEntity);
        testRepository.save(testEntity);

        // xoa tag
        List<TestTagEntity> list = testTagRepository.findAllByTestId(id);
        testTagRepository.deleteAll(list);

        // xoa cau hoi
        List<TestQuestionEntity> list1 = testQuestionRepository.findAllByTestId(id);
        testQuestionRepository.deleteAll(list1);

        List<QuestionEntity> questionEntityList = request.getQuestionList();
        List<TagEntity> tagEntityList = request.getTagList();
        // save test_question
        for (QuestionEntity questionEntity : questionEntityList) {
            TestQuestionEntity testQuestionEntity = new TestQuestionEntity();
            testQuestionEntity.setTestId(testEntity.getId());
            testQuestionEntity.setQuestionId(questionEntity.getId());

            testQuestionRepository.save(testQuestionEntity);
        }

        // save test_tag
        for (TagEntity tag : tagEntityList) {
            TestTagEntity testTagEntity = new TestTagEntity();
            testTagEntity.setTestId(testEntity.getId());
            testTagEntity.setTagId(tag.getId());

            testTagRepository.save(testTagEntity);
        }

        return request;
    }

    public void delete(Long id) {
        Optional<TestEntity> optional = testRepository.findById(id);
        TestEntity testEntity = optional.orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, Const.TAG_NOT_FOUND));
        testRepository.delete(testEntity);

        // xoa tag
        List<TestTagEntity> list = testTagRepository.findAllByTestId(id);
        testTagRepository.deleteAll(list);

        // xoa cau hoi
        List<TestQuestionEntity> list1 = testQuestionRepository.findAllByTestId(id);
        testQuestionRepository.deleteAll(list1);
    }
}
