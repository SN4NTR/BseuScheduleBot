package com.example.schedule.service;

import com.example.schedule.constant.Day;
import com.example.schedule.model.Schedule;
import com.example.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.example.schedule.converter.ScheduleConverter.scheduleToString;
import static com.example.schedule.converter.ScheduleConverter.stringToSchedule;
import static com.example.schedule.parser.ScheduleParser.getHtmlDocument;
import static com.example.schedule.parser.ScheduleParser.parseSchedulePage;

/**
 * @author Aliaksandr Miron
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Override
    public String getForWeek() {
        if (scheduleIsNotSaved()) {
            requestSchedule();
        }

        return getWeekSchedule();
    }

    @Override
    public String getForDay(Day day) {
        if (scheduleIsNotSaved()) {
            requestSchedule();
        }

        return getScheduleByDay(day);
    }

    private void requestSchedule() {
        Map<String, String> bodyData = getBodyData();
        Document htmlDocument = getHtmlDocument(bodyData).orElseThrow();
        String scheduleString = parseSchedulePage(htmlDocument);
        Schedule schedule = stringToSchedule(scheduleString);
        scheduleRepository.save(schedule);
    }

    private boolean scheduleIsNotSaved() {
        return scheduleRepository.findAll().isEmpty();
    }

    private String getWeekSchedule() {
        Schedule schedule = scheduleRepository.findById(1).orElseThrow();
        return scheduleToString(schedule);
    }

    private String getScheduleByDay(Day day) {
        Schedule schedule = scheduleRepository.findById(1).orElseThrow();
        return switch (day) {
            case MONDAY -> schedule.getMonday();
            case TUESDAY -> schedule.getTuesday();
            case WEDNESDAY -> schedule.getWednesday();
            case THURSDAY -> schedule.getThursday();
            case FRIDAY -> schedule.getFriday();
            case SATURDAY -> schedule.getSaturday();
        };
    }

    private Map<String, String> getBodyData() {
        Map<String, String> bodyParameters = new HashMap<>();
        bodyParameters.put("faculty", "7");
        bodyParameters.put("form", "10");
        bodyParameters.put("course", "3");
        bodyParameters.put("group", "7183");
        return bodyParameters;
    }
}
