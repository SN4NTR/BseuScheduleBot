package com.example.schedule.service;

import com.example.schedule.model.Schedule;
import com.example.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.schedule.constant.ScheduleConstant.SCHEDULE_HOST;
import static com.example.schedule.constant.ScheduleConstant.SCHEDULE_TABLE_CLASS_NAME;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBetween;

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
        return isScheduleSaved() ? getSchedule() : parseSchedule();
    }

    @Override
    public String getForDay(Integer dayNumber) {
        Schedule schedule = scheduleRepository.findById(1).orElseThrow();

        return switch (dayNumber) {
            case 1 -> schedule.getMonday();
            case 2 -> schedule.getTuesday();
            case 3 -> schedule.getWednesday();
            case 4 -> schedule.getThursday();
            case 5 -> schedule.getFriday();
            case 6 -> schedule.getSaturday();
            default -> "";
        };
    }

    private boolean isScheduleSaved() {
        return !scheduleRepository.findAll().isEmpty();
    }

    private String getSchedule() {
        Schedule schedule = scheduleRepository.findById(1).orElseThrow();
        return schedule.getMonday() + "\n\n"
                + schedule.getTuesday() + "\n\n"
                + schedule.getWednesday() + "\n\n"
                + schedule.getThursday() + "\n\n"
                + schedule.getFriday() + "\n\n"
                + schedule.getSaturday() + "\n\n";
    }

    private String parseSchedule() {
        Document pageDocument = getPageDocument().orElseThrow();
        Elements scheduleTables = pageDocument.getElementsByClass(SCHEDULE_TABLE_CLASS_NAME);
        Element scheduleTableForWeek = scheduleTables.get(0);
        Elements rows = scheduleTableForWeek.select("tr");

        StringBuilder schedule = new StringBuilder();

        for (Element row : rows) {
            Elements columns = row.select("td");

            if (isNameOfDay(columns)) {
                Element column = columns.get(0);

                String nameOfDay = column.text();
                String boldNameOfDay = getBoldText(nameOfDay);

                if (!boldNameOfDay.isEmpty()) {
                    schedule.append("\n")
                            .append(boldNameOfDay)
                            .append("\n");
                }

                continue;
            }

            for (Element column : columns) {
                String text = column.text();
                if (text.isEmpty()) {
                    continue;
                }

                addEmptyLineIfNeeded(schedule, text);
                schedule.append(text)
                        .append("\n");
            }
        }

        String scheduleString = schedule.toString();
        save(scheduleString);

        return scheduleString;
    }

    private void save(String scheduleText) {
        Schedule schedule = convertTextToSchedule(scheduleText);
        scheduleRepository.save(schedule);
    }

    private Schedule convertTextToSchedule(String scheduleText) {
        String monday = "<b>" + substringBetween(scheduleText, "<b>", "\n\n<b>вторник");
        String tuesday = "<b>вторник" + substringBetween(scheduleText, "<b>вторник", "\n\n<b>среда");
        String wednesday = "<b>среда" + substringBetween(scheduleText, "<b>среда", "\n\n<b>четверг");
        String thursday = "<b>четверг" + substringBetween(scheduleText, "<b>четверг", "\n\n<b>пятница");
        String friday = "<b>пятница" + substringBetween(scheduleText, "<b>пятница", "\n\n<b>суббота");
        String saturday = "<b>суббота" + substringAfter(scheduleText, "<b>суббота");

        return  Schedule.builder()
                .monday(monday)
                .tuesday(tuesday)
                .wednesday(wednesday)
                .thursday(thursday)
                .friday(friday)
                .saturday(saturday)
                .build();
    }

    private boolean isNameOfDay(Elements columns) {
        return columns.size() == 1;
    }

    private void addEmptyLineIfNeeded(StringBuilder schedule, String text) {
        Pattern timePattern = Pattern.compile("(\\d{2}:\\d{2}-\\d{2}:\\d{2})");
        Matcher timeMatcher = timePattern.matcher(text);

        if (timeMatcher.matches()) {
            schedule.append("\n");
        }
    }

    private String getBoldText(String text) {
        if (!text.isEmpty()) {
            return "<b>" + text + "</b>";
        }
        return "";
    }

    private Optional<Document> getPageDocument() {
        try {
            Map<String, String> bodyData = getBodyData();
            Document pageDocument = Jsoup.connect(SCHEDULE_HOST)
                    .data(bodyData)
                    .post();

            return Optional.of(pageDocument);
        } catch (IOException e) {
            String message = e.getMessage();
            log.error(message);

            return Optional.empty();
        }
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
