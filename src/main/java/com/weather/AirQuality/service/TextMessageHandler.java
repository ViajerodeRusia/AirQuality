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
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            TelegramBot bot = applicationContext.getBean(TelegramBot.class);
            AirQualityService airQualityService = applicationContext.getBean(AirQualityService.class);

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
                default:
                    // Логирование состояния
                    log.info("Received message: " + messageText + " from chatId: " + chatId);
                    log.info("Current waitingForCity state: " + messageText + " for chatId: " + chatId);

                    bot.sendMessage(chatId, "Checking air quality for " + messageText + "...");
                    airQualityService.sendAirQualityNow(chatId, messageText);
                break;
            }
        }
    }
}