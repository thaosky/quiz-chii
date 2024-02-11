package com.quizchii.common;

import com.quizchii.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CronResetStreakDay {

    UserService userService;

    public CronResetStreakDay(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetStreakDay() {
        System.out.println("============ RESET =================");
        userService.resetUserLazy();
    }

}
