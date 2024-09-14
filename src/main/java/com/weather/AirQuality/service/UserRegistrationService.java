package com.weather.AirQuality.service;

import com.weather.AirQuality.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.weather.AirQuality.entity.User;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserRegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationContext applicationContext;

    private Map<Long, Boolean> awaitingName = new HashMap<>();

    public void registerUser(Update update) {
        long chatId = update.getMessage().getChatId();
        TelegramBot bot = applicationContext.getBean(TelegramBot.class);
        if (userRepository.findByChatId(chatId).isPresent()) {
            bot.sendMessage(chatId, "Вы уже зарегистрированы как волонтер.");
        } else {
            askForName(chatId);
        }
    }

    public void registerUser(long chatId) {
        TelegramBot bot = applicationContext.getBean(TelegramBot.class);
        if (userRepository.findByChatId(chatId).isPresent()) {
            bot.sendMessage(chatId, "Вы уже зарегистрированы");
        } else {
            askForName(chatId);
        }
    }

    private void askForName(long chatId) {
        TelegramBot bot = applicationContext.getBean(TelegramBot.class);
        bot.sendMessage(chatId, "Пожалуйста, введите ваше имя:");
        awaitingName.put(chatId, true);
    }

    public void handleTextMessage(Update update) {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        TelegramBot bot = applicationContext.getBean(TelegramBot.class);

        if (isAwaitingName(chatId)) {
            saveName(chatId, messageText);
            finishRegistration(chatId);
        } else {
            bot.sendMessage(chatId, "Извините, указанная команда не распознана");
        }
    }

    protected boolean isAwaitingName(long chatId) {
        return awaitingName.containsKey(chatId) && awaitingName.get(chatId);
    }

    private void saveName(long chatId, String name) {
        User user = new User();
        user.setChatId(chatId);
        user.setName(name);
        userRepository.save(user);
    }

    private void finishRegistration(long chatId) {
        TelegramBot bot = applicationContext.getBean(TelegramBot.class);
        bot.sendMessage(chatId, "Регистрация завершена");
        awaitingName.remove(chatId);
    }
}