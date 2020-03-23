package com.example.schedule.parser;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.schedule.constant.ScheduleConstant.SCHEDULE_HOST;
import static com.example.schedule.constant.ScheduleConstant.SCHEDULE_TABLE_CLASS_NAME;
import static lombok.AccessLevel.PRIVATE;

/**
 * @author Aliaksandr Miron
 */
@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class ScheduleParser {

    public static String parseSchedulePage(Document htmlDocument) {
        Elements scheduleTables = htmlDocument.getElementsByClass(SCHEDULE_TABLE_CLASS_NAME);
        Element scheduleTableForWeek = scheduleTables.get(0);
        Elements rows = scheduleTableForWeek.select("tr");

        StringBuilder schedule = new StringBuilder();

        for (Element row : rows) {
            Elements columns = row.select("td");

            if (isWeekDay(columns)) {
                Element column = columns.get(0);

                String weekDay = column.text();
                String boldWeekDay = getBoldText(weekDay);

                if (!boldWeekDay.isEmpty()) {
                    schedule.append("\n")
                            .append(boldWeekDay)
                            .append("\n");
                }

                continue;
            }

            for (Element column : columns) {
                String text = column.text();
                if (text.isEmpty()) {
                    continue;
                }

                addNewLineIfNeeded(schedule, text);
                String italicText = getItalicText(text);

                schedule.append(italicText)
                        .append("\n");
            }
        }

        return schedule.toString();
    }

    public static Optional<Document> getHtmlDocument(Map<String, String> bodyData) {
        try {
            Document htmlDocument = Jsoup.connect(SCHEDULE_HOST)
                    .data(bodyData)
                    .post();

            return Optional.of(htmlDocument);
        } catch (IOException e) {
            String message = e.getMessage();
            log.error(message);
            return Optional.empty();
        }
    }

    private static boolean isTime(String text) {
        Pattern timePattern = Pattern.compile("(\\d{2}:\\d{2}-\\d{2}:\\d{2})");
        Matcher timeMatcher = timePattern.matcher(text);
        return timeMatcher.matches();
    }

    private static void addNewLineIfNeeded(StringBuilder schedule, String text) {
        schedule.append(isTime(text) ? "\n" : "");
    }

    private static String getBoldText(String text) {
        return text.isEmpty() ? "" : ("<b>" + text + "</b>");
    }

    private static String getItalicText(String text) {
        return isTime(text) ? ("<i>" + text + "</i>") : text;
    }

    private static boolean isWeekDay(Elements columns) {
        return columns.size() == 1;
    }
}
