package ru.naumen.bot.controller.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naumen.bot.command.Commands;
import ru.naumen.bot.configuration.ApplicationConfig;
import ru.naumen.bot.controller.BotController;
import ru.naumen.bot.processor.CommandBotProcessor;
import ru.naumen.bot.processor.MessageBotProcessor;

import java.util.Arrays;

/**
 * Класс {@link  TelegramBotController} отвечает за управление телеграмм-ботом и взаимодействие с ним.
 */
@Component
public class TelegramBotController implements BotController {

    /**
     * Экземпляр бота Телеграмм
     */
    private final TelegramBot telegramBot;

    /**
     * Процессор для обработки сообщений от пользователей
     */
    private final MessageBotProcessor messageBotProcessor;

    /**
     * Процессор для обработки команд от пользователей
     */
    private final CommandBotProcessor commandBotProcessor;

    /**
     * Логгер для записи сообщений об ошибках
     */
    private final Logger logger = LoggerFactory.getLogger(TelegramBotController.class);

    /**
     * Конструктор {@link  TelegramBotController} инициализирует бота Telegram, устанавливает его команды
     * и настраивает слушатель обновлений для обработки входящих обновлений.
     *
     * @param applicationConfig   конфигурация, содержащая токен бота Telegram
     * @param messageBotProcessor процессор для обработки сообщений от пользователей
     * @param commandBotProcessor процессор для обработки команд от пользователей
     */
    public TelegramBotController(ApplicationConfig applicationConfig, MessageBotProcessor messageBotProcessor,
                                 CommandBotProcessor commandBotProcessor) {
        this.telegramBot = new TelegramBot(applicationConfig.telegramToken());
        this.messageBotProcessor = messageBotProcessor;
        this.commandBotProcessor = commandBotProcessor;

        this.telegramBot.execute(new SetMyCommands(createCommandsMenu()));
        this.telegramBot.setUpdatesListener(updates -> {
            updates.forEach(this::processUpdate);
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
     * Создание меню команд для бота
     *
     * @return массив команд
     */
    private BotCommand[] createCommandsMenu() {
        return Arrays.stream(Commands.values())
                .map(command -> new BotCommand(command.getCommand(), command.getDescription()))
                .toArray(BotCommand[]::new);
    }

    /**
     * Обработка обновления, полученного от Телеграмм
     *
     * @param update обновление, содержащее информацию о сообщении от пользователя
     */
    private void processUpdate(Update update) {
        long chatId = update.message().chat().id();
        String message = update.message().text();
        if (message != null) {
            if (!commandBotProcessor.isChatActiveOrStarting(message, chatId)) {
                return;
            }
            if (message.startsWith("/")) {
                commandBotProcessor.processCommand(message, chatId);
            } else {
                messageBotProcessor.processMessage(message, chatId);
            }
        }
    }

    /**
     * Отправка сообщения в указанный чат
     *
     * @param message сообщение, которое будет отправлено
     * @param chatId  идентификатор чата, в который будет отправлено сообщение
     */
    @Override
    public void sendMessage(String message, long chatId) {
        telegramBot.execute(new SendMessage(chatId, message));
    }

}
