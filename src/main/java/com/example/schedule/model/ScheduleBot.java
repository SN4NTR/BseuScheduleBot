package com.example.schedule.model;

import com.example.schedule.service.BotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author Aliaksandr Miron
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ScheduleBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String name;

    @Value("${bot.token}")
    private String token;

    private final BotService botService;

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = botService.getChatId(update).orElseThrow();
        String incomingMessage = botService.getIncomingMessage(update).orElseThrow();
        ReplyKeyboardMarkup keyboard = botService.createKeyboardMarkup(incomingMessage);

        SendMessage outgoingMessage = botService.createOutgoingMessage(chatId, incomingMessage);
        outgoingMessage.setReplyMarkup(keyboard);
        sendMessage(outgoingMessage);
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            String errorMessage = e.getMessage();
            log.error(errorMessage);
        }
    }
}
