package com.weather.AirQuality.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

@Component
@Slf4j
public class TextMessageHandler {

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Autowired
    private ApplicationContext applicationContext;

    public void handleTextMessage(Update update) throws TelegramApiException, IOException {
        // Check if it's a text message
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            TelegramBot bot = applicationContext.getBean(TelegramBot.class);

            switch (messageText) {
                case "/start":
                    bot.startCommandReceived(chatId, update.getMessage().getChat().getFirstName());

                    break;
                case "/help":
                    bot.sendMessage(chatId, "List of commands:" +
                            "\n/start - Menu" +
                            "\n/register_user - User registration" +
                            "\n/help - List of commands");
                    break;
                case "/register_user":
                    userRegistrationService.registerUser(update);
                    break;
                default:
                    bot.sendMessage(chatId, "Please choose one of the listed commands");
                    break;
            }
        }
    }
}
