package com.quizchii.service;

import com.quizchii.common.BusinessException;
import com.quizchii.common.MessageCode;
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
    private final AchievementService achievementService;

    public ResultResponse submitTest(ResultRequest request) {

        Optional<TestEntity> optionalTest = testRepository.findById(request.getTestId());
        TestEntity test = optionalTest.orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.TEST_NOT_FOUND));

        List<ResultDetailRequest> resultList = request.getResultDetails();
        List<QuestionEntity> questionList = questionRepository.findAllByTestId(test.getId());

        UserEntity userEntity = userRepository.findById(request.getUserId()).orElseThrow(
                () -> new BusinessException(HttpStatus.NOT_FOUND, MessageCode.USER_NOT_EXIST)
        );

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
            resultDetailEntity.setExplanation(questionList.get(i).getExplanation());

            resultDetailEntities.add(resultDetailEntity);
        }


        // Lưu kết quả thi (Ngày, giờ, điểm)
        ResultEntity resultEntity = new ResultEntity();
        Timestamp statedAt = Util.convertStringToTimestamp(request.getStartedAt());
        resultEntity.setStartedAt(statedAt);
        resultEntity.setTestName(request.getTestName());
        resultEntity.setTotalQuestion(questionList.size());
        Timestamp submittedAt = Util.convertStringToTimestamp(request.getSubmittedAt());
        resultEntity.setSubmittedAt(submittedAt);
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


        ResultResponse response = new ResultResponse();

        /*
         * Streak
         */
        // Streak day
        int streakDays = calculateStreakDays(userEntity.getLastActive(), submittedAt);
        // First submit or not
        if (isFistSubmitOnDay(userEntity.getLastActive(), submittedAt)) {
            response.setFirstSubmit(true);
            userEntity.setTotalDaysStreak(streakDays);
            response.setMessageStreak(String.format(MessageCode.ACHIEVEMENT_DAILY_CONGRA, streakDays));

            //Check nhận achievement
            achievementService.createAchievement(userEntity.getId(), streakDays);
        }
        userEntity.setLastActive(submittedAt);
        userRepository.save(userEntity);

        // Trả lại kết quả
        response.setCorrected(point);
        response.setTestName(test.getName());
        response.setStartedAt(request.getStartedAt());
        response.setUserId(request.getUserId());
        response.setUsername(userEntity.getUsername());
        response.setSubmittedAt(request.getSubmittedAt());
        response.setResultDetails(detailResponses);
        response.setResultId(saved.getId());
        return response;
    }

    public ListResultResponse listResultByUserId(Long id) {
        ListResultResponse response = new ListResultResponse();

        if (!authService.havePermission(id)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, MessageCode.FORBIDDEN);
        }

        List<ListResultItemResponse> list = new ArrayList<>();
        List<ResultEntity> resultEntityList = resultRepository.getAllByAccountId(id);
        for (ResultEntity entity : resultEntityList) {
            ListResultItemResponse item = new ListResultItemResponse();
            BeanUtils.copyProperties(entity, item);
            item.setResultId(entity.getId());
            item.setStartedAt(entity.getStartedAt().toString());
            item.setSubmittedAt(entity.getSubmittedAt().toString());
            item.setResultId(entity.getId());
            item.setTotalQuestion(entity.getTotalQuestion());
            item.setTestName(entity.getTestName());
            list.add(item);
        }

        response.setList(list);

        UserEntity userEntity = userRepository.findById(id).orElseThrow(
                () -> new BusinessException(HttpStatus.NOT_FOUND, MessageCode.USER_NOT_EXIST)
        );
        // Todo
        response.setUsername(userEntity.getUsername());
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
            item.setStartedAt(entity.getStartedAt().toString());
            item.setSubmittedAt(entity.getSubmittedAt().toString());
            item.setTotalQuestion(entity.getTotalQuestion());
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

    /**
     * @param id kết quả lần thi
     * @return
     */
    public ResultResponse getResultDetail(Long id) {
        // Lấy thông tin kết quả lần thi
        ResultResponse response = new ResultResponse();
        ResultEntity resultEntity = resultRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, MessageCode.RESULT_NOT_FOUND));

        response.setUserId(resultEntity.getAccountId());
        response.setResultId(resultEntity.getId());

        UserEntity userEntity = userRepository.findById(resultEntity.getAccountId()).orElseThrow(
                () -> new BusinessException(HttpStatus.NOT_FOUND, MessageCode.USER_NOT_EXIST)
        );

        response.setStartedAt(resultEntity.getStartedAt().toString());
        response.setTestName(resultEntity.getTestName());
        response.setSubmittedAt(resultEntity.getSubmittedAt().toString());
        response.setCorrected(resultEntity.getCorrected());
        response.setUsername(userEntity.getUsername());

        // Thông tin chi tiết lần thi
        List<ResultDetailResponse> list = new ArrayList<>();
        List<ResultDetailEntity> entities = resultDetailRepository.findAllByResultId(id);
        for (ResultDetailEntity entity : entities) {
            ResultDetailResponse item = new ResultDetailResponse();
            BeanUtils.copyProperties(entity, item);

            list.add(item);
        }

        response.setResultDetails(list);
        return response;
    }


    // Xem da hoc bao nhieu ngay lien tiep
    private int calculateStreakDays(Timestamp lastActive, Timestamp currentTime) {

        // Neu user chua hoc ngay nao => Set streak = 1
        if (lastActive == null) {
            return 1;
        }
        long time1 = lastActive.getTime();
        long time2 = currentTime.getTime();

        int res = Util.daysBetween(time1, time2);
        return res + 1;
    }


    private boolean isFistSubmitOnDay(Timestamp lastActive, Timestamp currentTime) {
        if (lastActive == null) return true;
        return !Util.isSameDay(lastActive, currentTime);
    }
}
