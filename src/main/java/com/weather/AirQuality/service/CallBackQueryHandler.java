package com.weather.AirQuality.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
public class CallBackQueryHandler {

    @Autowired
    private UserRegistrationService userRegistrationService;
    @Autowired
    private TextMessageHandler textMessageHandler;
    @Autowired
    private ApplicationContext applicationContext;

    public void handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        TelegramBot bot = applicationContext.getBean(TelegramBot.class);

        try {
            switch (callbackData) {
                case "Air quality check daily":
                    bot.sendMessage(chatId, "Please enter the city for daily air quality check:");
                    break;
                case "Air quality check":
                    bot.sendMessage(chatId, "Please enter the city to check the air quality:");
                    break;
                default:
                    log.warn("Unknown callback data received: " + callbackData);
                    bot.sendMessage(chatId, "Unknown command. Please try again.");
            }
        } catch (Exception e) {
            log.error("Error while handling callback query: " + callbackData, e);
            bot.sendMessage(chatId, "An error occurred while processing your request. Please try again later.");
        }
    }
}