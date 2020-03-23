package com.example.schedule.converter;

import com.example.schedule.model.Schedule;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBetween;

/**
 * @author Aliaksandr Miron
 */
@NoArgsConstructor(access = PRIVATE)
public class ScheduleConverter {

    public static Schedule stringToSchedule(String scheduleText) {
        String monday = "<b>Понедельник" + substringBetween(scheduleText, "<b>понедельник", "\n\n<b>вторник");
        String tuesday = "<b>Вторник" + substringBetween(scheduleText, "<b>вторник", "\n\n<b>среда");
        String wednesday = "<b>Среда" + substringBetween(scheduleText, "<b>среда", "\n\n<b>четверг");
        String thursday = "<b>Четверг" + substringBetween(scheduleText, "<b>четверг", "\n\n<b>пятница");
        String friday = "<b>Пятница" + substringBetween(scheduleText, "<b>пятница", "\n\n<b>суббота");
        String saturday = "<b>Суббота" + substringAfter(scheduleText, "<b>суббота");

        return  Schedule.builder()
                .monday(monday)
                .tuesday(tuesday)
                .wednesday(wednesday)
                .thursday(thursday)
                .friday(friday)
                .saturday(saturday)
                .build();
    }

    public static String scheduleToString(Schedule schedule) {
        return schedule.getMonday() + "\n\n" +
                schedule.getTuesday() + "\n\n" +
                schedule.getWednesday() + "\n\n" +
                schedule.getThursday() + "\n\n" +
                schedule.getFriday() + "\n\n" +
                schedule.getSaturday();
    }
}
