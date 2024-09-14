package com.weather.AirQuality.service;

import com.weather.AirQuality.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final CallBackQueryHandler callBackQueryHandler;
    private final TextMessageHandler textMessageHandler;

    @Autowired
    public TelegramBot(BotConfig botConfig, CallBackQueryHandler callBackQueryHandler, TextMessageHandler textMessageHandler) {
        this.botConfig = botConfig;
        this.callBackQueryHandler = callBackQueryHandler;
        this.textMessageHandler = textMessageHandler;

        // Init of bot commands
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Start"));
        listOfCommands.add(new BotCommand("/help", "Help"));

        try {
            // Set commands for Telegram bot
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error with the list of commands", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            callBackQueryHandler.handleCallbackQuery(update);
        } else {
            try {
                textMessageHandler.handleTextMessage(update);
            } catch (TelegramApiException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!";
        log.info("Replied to " + name);
        sendMessage(chatId, answer);
        choosingMenu(chatId);
    }

    public void sendMessage(long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error occurred while sending message: " + e.getMessage(), e);
        }
    }
    public void choosingMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Choose what you need:");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();

        InlineKeyboardButton buttonMenu1 = new InlineKeyboardButton("Air quality check");
        buttonMenu1.setText("Check Air Quality");
        buttonMenu1.setCallbackData("Air quality check");

        rowInline1.add(buttonMenu1);

        rowsInline.add(rowInline1);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
            log.info("Sent a message to the chat: " + chatId);
        } catch (TelegramApiException e) {
            log.error("Error in menu: " + e.getMessage(), e);
        }
    }
}
