package com.example.schedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.schedule.constant.ButtonConstant.ALL_WEEK_BUTTON;
import static com.example.schedule.constant.ButtonConstant.BACK_BUTTON;
import static com.example.schedule.constant.ButtonConstant.CURRENT_WEEK_BUTTON;
import static com.example.schedule.constant.ButtonConstant.FRIDAY_BUTTON;
import static com.example.schedule.constant.ButtonConstant.GET_SCHEDULE_BUTTON;
import static com.example.schedule.constant.ButtonConstant.MONDAY_BUTTON;
import static com.example.schedule.constant.ButtonConstant.NEXT_WEEK_BUTTON;
import static com.example.schedule.constant.ButtonConstant.SATURDAY_BUTTON;
import static com.example.schedule.constant.ButtonConstant.START_BUTTON;
import static com.example.schedule.constant.ButtonConstant.THURSDAY_BUTTON;
import static com.example.schedule.constant.ButtonConstant.TUESDAY_BUTTON;
import static com.example.schedule.constant.ButtonConstant.WEDNESDAY_BUTTON;
import static com.example.schedule.constant.Day.FRIDAY;
import static com.example.schedule.constant.Day.MONDAY;
import static com.example.schedule.constant.Day.SATURDAY;
import static com.example.schedule.constant.Day.THURSDAY;
import static com.example.schedule.constant.Day.TUESDAY;
import static com.example.schedule.constant.Day.WEDNESDAY;

/**
 * @author Aliaksandr Miron
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BotServiceImpl implements BotService {

    private final ScheduleService scheduleService;

    @Override
    public Optional<Long> getChatId(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        return Optional.of(chatId);
    }

    @Override
    public Optional<String> getIncomingMessage(Update update) {
        Message message = update.getMessage();
        String firstName = message.getChat().getFirstName();
        String lastName = message.getChat().getLastName();
        log.info("User: {} {}", firstName, lastName);
        String text = message.getText();
        return Optional.of(text);
    }

    @Override
    public SendMessage createOutgoingMessage(Long chatId, String incomingMessage) {
        return switch (incomingMessage) {
            case MONDAY_BUTTON -> new SendMessage(chatId, scheduleService.getForDay(MONDAY));
            case TUESDAY_BUTTON -> new SendMessage(chatId, scheduleService.getForDay(TUESDAY));
            case WEDNESDAY_BUTTON -> new SendMessage(chatId, scheduleService.getForDay(WEDNESDAY));
            case THURSDAY_BUTTON -> new SendMessage(chatId, scheduleService.getForDay(THURSDAY));
            case FRIDAY_BUTTON -> new SendMessage(chatId, scheduleService.getForDay(FRIDAY));
            case SATURDAY_BUTTON -> new SendMessage(chatId, scheduleService.getForDay(SATURDAY));
            case ALL_WEEK_BUTTON -> new SendMessage(chatId, scheduleService.getForWeek());
            case GET_SCHEDULE_BUTTON, BACK_BUTTON, START_BUTTON -> new SendMessage(chatId, "Выберите неделю");
            case CURRENT_WEEK_BUTTON, NEXT_WEEK_BUTTON -> new SendMessage(chatId, "Выберите день недели");
            default -> new SendMessage(chatId, "Неверная команда");
        };
    }

    @Override
    public ReplyKeyboardMarkup createKeyboardMarkup(String incomingMessage) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRow = createKeyboard(incomingMessage);
        keyboardMarkup.setKeyboard(keyboardRow);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    private List<KeyboardRow> createKeyboard(String incomingMessage) {
        List<KeyboardRow> keyboard = new ArrayList<>();

        switch (incomingMessage) {
            case GET_SCHEDULE_BUTTON, BACK_BUTTON -> {
                KeyboardRow currentWeekButton = new KeyboardRow();
                currentWeekButton.add(CURRENT_WEEK_BUTTON);

                KeyboardRow nextWeekButton = new KeyboardRow();
                nextWeekButton.add(NEXT_WEEK_BUTTON);

                keyboard.addAll(List.of(currentWeekButton, nextWeekButton));
                return keyboard;
            }
            case CURRENT_WEEK_BUTTON, NEXT_WEEK_BUTTON, MONDAY_BUTTON, TUESDAY_BUTTON, WEDNESDAY_BUTTON,
                    THURSDAY_BUTTON, FRIDAY_BUTTON, SATURDAY_BUTTON, ALL_WEEK_BUTTON -> {

                KeyboardRow allWeekButton = new KeyboardRow();
                allWeekButton.add(ALL_WEEK_BUTTON);

                KeyboardRow daysButton = new KeyboardRow();
                daysButton.addAll(List.of(MONDAY_BUTTON, TUESDAY_BUTTON, WEDNESDAY_BUTTON, THURSDAY_BUTTON, FRIDAY_BUTTON, SATURDAY_BUTTON));

                KeyboardRow backButton = new KeyboardRow();
                backButton.add(BACK_BUTTON);

                keyboard.addAll(List.of(allWeekButton, daysButton, backButton));
                return keyboard;
            }
            default -> {
                KeyboardRow getScheduleButton = new KeyboardRow();
                getScheduleButton.add(GET_SCHEDULE_BUTTON);
                keyboard.add(getScheduleButton);
                return keyboard;
            }
        }
    }
}
