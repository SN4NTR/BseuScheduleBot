package com.example.schedule.service;

import java.io.IOException;

/**
 * @author Aliaksandr Miron
 */
public interface ScheduleService {

    String getForWeek();

    String getForDay(Integer dayNumber);
}
