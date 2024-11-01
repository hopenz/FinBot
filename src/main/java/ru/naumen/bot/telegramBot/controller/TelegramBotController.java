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

/**
 * Класс {@link  TelegramBotController} отвечает за управление ботом и взаимодействие с ним.
 */
@Component
public class TelegramBotController {

    /**
     * Экземпляр бота Телеграмм
     */
    private final TelegramBot telegramBot;

    /**
     * Логгер для записи сообщений об ошибках
     */
    private final Logger logger = LoggerFactory.getLogger(TelegramBotController.class);

    /**
     * Конструктор {@link  TelegramBotController} инициализирует бота Telegram, устанавливает его команды
     * и настраивает слушатель обновлений для обработки входящих обновлений.
     *
     * @param applicationConfig  конфигурация, содержащая токен бота Telegram
     * @param applicationContext контекст приложения, используемый для получения бинов
     */
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

    /**
     * Отправка сообщения в указанный чат
     *
     * @param message сообщение, которое будет отправлено
     * @param chatId  идентификатор чата, в который будет отправлено сообщение
     */
    public void sendMessage(String message, long chatId) {
        telegramBot.execute(new SendMessage(chatId, message));
    }

}
