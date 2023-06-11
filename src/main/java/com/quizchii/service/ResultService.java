package com.quizchii.service;

import com.quizchii.common.BusinessException;
import com.quizchii.common.StatusCode;
import com.quizchii.entity.QuestionEntity;
import com.quizchii.entity.ResultDetailEntity;
import com.quizchii.entity.ResultEntity;
import com.quizchii.entity.TestEntity;
import com.quizchii.model.request.ResultDetailRequest;
import com.quizchii.model.request.ResultRequest;
import com.quizchii.model.response.ResultDetailResponse;
import com.quizchii.model.response.ResultResponse;
import com.quizchii.repository.QuestionRepository;
import com.quizchii.repository.ResultDetailRepository;
import com.quizchii.repository.ResultRepository;
import com.quizchii.repository.TestRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
        resultEntity.setStartedAt(request.getStartedAt());
        resultEntity.setSubmitAt(request.getSubmitAt());
        resultEntity.setAccountId(request.getUserId());
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
        response.setStartedAt(request.getSubmitAt());
        response.setResultDetails(detailResponses);
        return response;
    }
}
