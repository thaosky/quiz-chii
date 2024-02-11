//package com.quizchii.common;
//
//import com.quizchii.entity.UserEntity;
//import com.quizchii.repository.UserRepository;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class CronResetStreakDay {
//
//    UserRepository userRepository;
//
//    public CronResetStreakDay(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Scheduled(cron = "0 0 0 * * ?")
//    public void resetStreakDay() {
//        List<UserEntity> userEntityList = userRepository.findAll();
//    }
//
//}
