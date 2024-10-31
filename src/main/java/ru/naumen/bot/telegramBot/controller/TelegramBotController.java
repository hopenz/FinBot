package ru.naumen.bot.telegramBot.controller;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.naumen.bot.configuration.ApplicationConfig;
import ru.naumen.bot.telegramBot.command.Commands;
import ru.naumen.bot.telegramBot.processor.UpdateBotProcessor;

@Component
public class TelegramBotController {

    private final TelegramBot telegramBot;

    private final Logger logger = LoggerFactory.getLogger(TelegramBotController.class);

    public TelegramBotController(ApplicationConfig applicationConfig, ApplicationContext applicationContext) {
        this.telegramBot = new TelegramBot(applicationConfig.telegramToken());
        this.telegramBot.execute(new SetMyCommands(
                new Commands().getCommands()
        ));
        this.telegramBot.setUpdatesListener(updates -> {
            updates.forEach(applicationContext.getBean(UpdateBotProcessor.class)::processUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, exception -> {
            if (exception.response() != null) {
                logger.error(
                        "[Bot exception] :: Code: {}; Message: {}.",
                        exception.response().errorCode(),
                        exception.response().description()
                );
            } else {
                logger.error("[Unexpected exception] :: Message: {}.", exception.getMessage());
            }
        });
    }

    public void sendMessage(String message, long chatId) {
        telegramBot.execute(new SendMessage(chatId, message));
    }

}
