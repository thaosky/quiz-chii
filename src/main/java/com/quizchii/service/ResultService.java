package com.quizchii.service;

import com.quizchii.common.BusinessException;
import com.quizchii.common.StatusCode;
import com.quizchii.common.Util;
import com.quizchii.entity.*;
import com.quizchii.model.request.ResultDetailRequest;
import com.quizchii.model.request.ResultRequest;
import com.quizchii.model.response.*;
import com.quizchii.repository.*;
import com.quizchii.security.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;
    private final ResultDetailRepository resultDetailRepository;
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final AuthService authService;
    private final UserRepository userRepository;

    public ResultResponse submitTest(ResultRequest request) {

        Optional<TestEntity> optional = testRepository.findById(request.getTestId());
        TestEntity test = optional.orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, StatusCode.TEST_NOT_FOUND));

        List<ResultDetailRequest> resultList = request.getResultDetails();
        List<QuestionEntity> questionList = questionRepository.findAllByTestId(test.getId());

        // Lưu kết quả thi chi tiết
        // Duyệt qua từng câu trả lời của user
        List<ResultDetailEntity> resultDetailEntities = new ArrayList<>();

        // Tính điểm
        int point = 0;
        for (int i = 0; i < questionList.size(); i++) {
            if (questionList.get(i).getCorrectAnswer() == resultList.get(i).getAnswered()) {
                point++;
            }
            ResultDetailEntity resultDetailEntity = new ResultDetailEntity();

            resultDetailEntity.setQuestion(questionList.get(i).getQuestion());
            resultDetailEntity.setContent(questionList.get(i).getContent());
            resultDetailEntity.setAnswer1(questionList.get(i).getAnswer1());
            resultDetailEntity.setAnswer2(questionList.get(i).getAnswer2());
            resultDetailEntity.setAnswer3(questionList.get(i).getAnswer3());
            resultDetailEntity.setAnswer4(questionList.get(i).getAnswer4());
            resultDetailEntity.setCorrectAnswer(questionList.get(i).getCorrectAnswer());
            resultDetailEntity.setAnswered(resultList.get(i).getAnswered());

            resultDetailEntities.add(resultDetailEntity);
        }


        // Lưu kết quả thi (Ngày, giờ, điểm)
        ResultEntity resultEntity = new ResultEntity();
        Timestamp statedAt = Util.convertStringToTimestamp(request.getStartedAt());
        resultEntity.setStartedAt(statedAt);
        resultEntity.setTestName(request.getTestName());
        Timestamp submittedAt = Util.convertStringToTimestamp(request.getSubmitAt());
        resultEntity.setSubmitAt(submittedAt);
        resultEntity.setAccountId(request.getUserId());
        resultEntity.setTestId(request.getTestId());
        resultEntity.setCorrected(point);
        ResultEntity saved = resultRepository.save(resultEntity);

        // Lưu id của kết quả cho kết quả chi tiết + trả về response kết quả chi tiết
        List<ResultDetailResponse> detailResponses = new ArrayList<>();
        for (ResultDetailEntity entity : resultDetailEntities) {
            entity.setResultId(saved.getId());
            ResultDetailResponse response = new ResultDetailResponse();
            BeanUtils.copyProperties(entity, response);
            detailResponses.add(response);
        }
        resultDetailRepository.saveAll(resultDetailEntities);

        // Trả lại kết quả
        ResultResponse response = new ResultResponse();
        response.setCorrected(point);
        response.setStartedAt(request.getStartedAt());
        response.setUserId(request.getUserId());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        response.setUsername(login);
        response.setSubmitAt(request.getSubmitAt());
        response.setResultDetails(detailResponses);
        return response;
    }

    public ListResultResponse listResultByUserId(Long id) {
        ListResultResponse response = new ListResultResponse();

        if (!authService.havePermission(id)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, StatusCode.FORBIDDEN);
        }

        List<ListResultItemResponse> list = new ArrayList<>();
        List<ResultEntity> resultEntityList = resultRepository.getAllByAccountId(id);
        for (ResultEntity entity : resultEntityList) {
            ListResultItemResponse item = new ListResultItemResponse();
            BeanUtils.copyProperties(entity, item);
            list.add(item);
        }

        response.setList(list);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        response.setUsername(login);
        response.setUserId(id);

        return response;
    }

    public ListResultByTestIdResponse listResultByTestId(Long id) {
        ListResultByTestIdResponse response = new ListResultByTestIdResponse();

        List<ListResultItemByTestIdResponse> list = new ArrayList<>();
        List<ResultEntity> resultEntityList = resultRepository.getAllByTestId(id);
        for (ResultEntity entity : resultEntityList) {
            ListResultItemByTestIdResponse item = new ListResultItemByTestIdResponse();
            BeanUtils.copyProperties(entity, item);
            item.setStartedAt();

            item.setUserId(entity.getAccountId());
            UserEntity userEntity = userRepository.getById(entity.getAccountId());
            item.setUsername(userEntity.getUsername());

            list.add(item);
        }

        // Get test by id test
        TestEntity test = testRepository.getById(id);

        response.setList(list);
        response.setTestName(test.getName());
        return response;
    }

//    public Object getResultDetail(Long id) {
//        ResultDetailResponse
//    }
}
