package com.quizchii.service;


import com.quizchii.Enum.TestType;
import com.quizchii.common.BusinessException;
import com.quizchii.common.MessageCode;
import com.quizchii.common.Util;
import com.quizchii.entity.*;
import com.quizchii.model.ListResponse;
import com.quizchii.model.response.TestResponse;
import com.quizchii.model.view.TestResponseView;
import com.quizchii.repository.*;
import com.quizchii.security.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
    private ResultRepository resultRepository;
    private AuthService authService;
    private UserRepository userRepository;

    public ListResponse<TestResponse> getAllTest(Integer pageSize,
                                                 Integer pageNo,
                                                 String sortName,
                                                 String sortDir,
                                                 String name,
                                                 Long tagId,
                                                 TestType testType) {
        // Paging & sorting
        if ("".equals(name)) {
            name = null;
        }
        Pageable pageable = Util.createPageable(pageSize, pageNo, sortName, sortDir);
        String testTypeParam = null;
        if (testType != null) {
            testTypeParam = testType.getValue();
        }
        Page<TestResponseView> page = testRepository.listTest(name, tagId, testTypeParam, pageable);

        ListResponse<TestResponse> response = new ListResponse();
        List<TestResponseView> entities = page.toList();
        List<TestResponse> testResponseList = new ArrayList<>();
        for (TestResponseView entity : entities) {
            TestResponse item = new TestResponse();
            BeanUtils.copyProperties(entity, item);
            List<TagEntity> tagEntityList = tagRepository.findAllByTestId(entity.getId());
            if (TestType.ONCE_WITH_TIME.equals(item.getTestType())) {
                item.setStartTime(Util.convertTimestampToString(entity.getStartTime()));
                item.setEndTime(Util.convertTimestampToString(entity.getEndTime()));
            }
            item.setTagList(tagEntityList);
            testResponseList.add(item);
        }
        response.setItems(testResponseList);
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotalElements((int) page.getTotalElements());
        response.setTotalPage(page.getTotalPages());

        return response;
    }

    public TestResponse viewTest(Long testId) {
        // Get test
        TestEntity testEntity = testRepository.findById(testId).get();
        TestResponse dto = new TestResponse();
        BeanUtils.copyProperties(testEntity, dto);

        // Get list question
        List<QuestionEntity> questionListForAdmin = questionRepository.findAllByTestId(testId);
        List<QuestionEntity> questionListForUser = new ArrayList<>();
        for(QuestionEntity question: questionListForAdmin) {
            QuestionEntity doneItem = new QuestionEntity();
            BeanUtils.copyProperties(question, doneItem, "correctAnswer");
            questionListForUser.add(doneItem);
        }

        // Get list tag
        List<TagEntity> tagEntityList = tagRepository.findAllByTestId(testId);

        dto.setQuestionList(questionListForUser);
        dto.setTagList(tagEntityList);
        return dto;
    }

    public TestResponse getById(Long testId, Long userId) {

        // Get test
        TestEntity testEntity = testRepository.findById(testId).get();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        UserEntity userEntity = userRepository.getByUsername(username);
        canAccessTest(testEntity, userEntity.getId());
        TestResponse dto = new TestResponse();
        BeanUtils.copyProperties(testEntity, dto);
        if (TestType.ONCE_WITH_TIME.equals(testEntity.getTestType())) {
            dto.setStartTime(testEntity.getStartTime().toString());
            dto.setEndTime(testEntity.getEndTime().toString());
        }
        // Get list question
        List<QuestionEntity> questionListForAdmin = questionRepository.findAllByTestId(testId);
        dto.setQuestionList(questionListForAdmin);

        boolean isAdmin = authService.isAdmin();
        if (!isAdmin) {
            List<QuestionEntity> questionListForUser = new ArrayList<>();
            for(QuestionEntity question: questionListForAdmin) {
                QuestionEntity doneItem = new QuestionEntity();
                BeanUtils.copyProperties(question, doneItem, "correctAnswer");
                questionListForUser.add(doneItem);
            }
            dto.setQuestionList(questionListForUser);
        }

        // Get list tag
        List<TagEntity> tagEntityList = tagRepository.findAllByTestId(testId);
        dto.setTagList(tagEntityList);
        return dto;
    }

    private boolean canAccessTest(TestEntity test, Long userId) {
        if (authService.isAdmin()) return true; // Nếu là admin luôn truy cập dc test
        if (TestType.ONCE_WITH_TIME.equals(test.getTestType()) || TestType.ONCE_WITHOUT_TIME.equals(test.getTestType())) {
            if (TestType.ONCE_WITH_TIME.equals(test.getTestType())) {
                Timestamp now = new Timestamp(new Date().getTime());
                if (now.before(test.getStartTime()) || now.after(test.getEndTime())) {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.TEST_NOT_VALID);
                }
            }

            List <ResultEntity> resultEntities = resultRepository.getAllByTestIdAndAccountId(test.getId(), userId);
            if (!resultEntities.isEmpty()) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.TEST_EXCEED_ONCE);
            }
        }
        return true;
    }


    public TestResponse create(TestResponse testResponse) {
        List<QuestionEntity> questionEntityList = testResponse.getQuestionList();
        List<TagEntity> tagEntityList = testResponse.getTagList();

        TestEntity testEntity = new TestEntity();
        BeanUtils.copyProperties(testResponse, testEntity);
        if (TestType.ONCE_WITH_TIME.equals(testEntity.getTestType())) {
            testEntity.setStartTime(Util.convertStringToTimestamp(testResponse.getStartTime()));
            testEntity.setEndTime(Util.addTime(testEntity.getStartTime(), testEntity.getAvailableTime()));
        }
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

        testResponse.setId(save.getId());
        return testResponse;
    }

    public TestResponse update(TestResponse request, Long id) {
        TestEntity testEntity = testRepository.findById(id).get();
        BeanUtils.copyProperties(request, testEntity);
        if (TestType.ONCE_WITH_TIME.equals(request.getTestType())) {
            testEntity.setStartTime(Util.convertStringToTimestamp(request.getStartTime()));
            testEntity.setEndTime(Util.addTime(testEntity.getStartTime(), request.getAvailableTime()));
        } else {
            testEntity.setStartTime(null);
            testEntity.setEndTime(null);
        }
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
        TestEntity testEntity = optional.orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, MessageCode.TAG_NOT_FOUND));
        testRepository.delete(testEntity);

        // xoa tag
        List<TestTagEntity> list = testTagRepository.findAllByTestId(id);
        testTagRepository.deleteAll(list);

        // xoa cau hoi
        List<TestQuestionEntity> list1 = testQuestionRepository.findAllByTestId(id);
        testQuestionRepository.deleteAll(list1);
    }

    public void deleteIds(List<Long> ids) {
        for (Long id: ids) {
            Optional<TestEntity> optional = testRepository.findById(id);
            TestEntity testEntity = optional.orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, MessageCode.TAG_NOT_FOUND));
            testRepository.delete(testEntity);

            // xoa tag
            List<TestTagEntity> list = testTagRepository.findAllByTestId(id);
            testTagRepository.deleteAll(list);

            // xoa cau hoi
            List<TestQuestionEntity> list1 = testQuestionRepository.findAllByTestId(id);
            testQuestionRepository.deleteAll(list1);
        }
    }
}
