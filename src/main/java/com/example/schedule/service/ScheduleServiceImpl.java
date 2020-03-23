package com.example.schedule.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.schedule.constant.ScheduleConstant.SCHEDULE_HOST;
import static com.example.schedule.constant.ScheduleConstant.SCHEDULE_TABLE_CLASS_NAME;

/**
 * @author Aliaksandr Miron
 */
@Slf4j
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Override
    public String getForWeek() {
        Document pageDocument = getPageDocument().orElseThrow();
        return parseSchedule(pageDocument);
    }

    @Override
    public String getForDay(Integer dayNumber) {
        return null;
    }

    private String parseSchedule(Document pageDocument) {
        Elements scheduleTables = pageDocument.getElementsByClass(SCHEDULE_TABLE_CLASS_NAME);
        Element scheduleTableForWeek = scheduleTables.get(0);
        Elements rows = scheduleTableForWeek.select("tr");

        StringBuilder schedule = new StringBuilder();

        for (Element row : rows) {
            Elements columns = row.select("td");

            if (columns.size() == 1) {
                Element column = columns.get(0);
                String day = column.text();
                String boldDayName = getBoldText(day);
                schedule.append("\n")
                        .append(boldDayName)
                        .append("\n");

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

        return schedule.toString();
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
