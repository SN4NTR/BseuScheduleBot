package com.example.schedule.service;

import com.example.schedule.constant.Day;

/**
 * @author Aliaksandr Miron
 */
public interface ScheduleService {

    String getForWeek();

    String getForDay(Day day);
}
