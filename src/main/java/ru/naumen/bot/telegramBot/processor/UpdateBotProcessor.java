package ru.naumen.bot.telegramBot.processor;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;
import ru.naumen.bot.telegramBot.controller.TelegramBotController;
import ru.naumen.bot.telegramBot.service.UserService;

import static ru.naumen.bot.telegramBot.command.Commands.COMMAND_PREFIX;
import static ru.naumen.bot.telegramBot.command.Commands.START_COMMAND;

/**
 * Класс  {@link  UpdateBotProcessor}, в котором происходит
 * обработка обновлений, полученных от Телеграмм
 */
@Component
public class UpdateBotProcessor {

    /**
     * Процессор для обработки сообщений от пользователей
     */
    private final MessageBotProcessor messageBotProcessor;

    /**
     * Процессор для обработки команд от пользователей
     */
    private final CommandBotProcessor commandBotProcessor;

    /**
     * Сервис для взаимодействия с данными пользователя
     */
    private final UserService userService;

    /**
     * Контроллер для взаимодействия с ботом Телеграмм
     */
    private final TelegramBotController telegramBotController;

    /**
     * Конструктор {@link UpdateBotProcessor}
     *
     * @param messageBotProcessor    процессор для обработки сообщений от пользователей
     * @param commandBotProcessor    процессор для обработки команд от пользователей
     * @param userService            сервис для взаимодействия с данными пользователя
     * @param telegramBotController1 контроллер для взаимодействия с ботом Телеграмм
     */
    public UpdateBotProcessor(MessageBotProcessor messageBotProcessor, CommandBotProcessor commandBotProcessor,
                              UserService userService, TelegramBotController telegramBotController1) {
        this.messageBotProcessor = messageBotProcessor;
        this.commandBotProcessor = commandBotProcessor;
        this.userService = userService;
        this.telegramBotController = telegramBotController1;
    }

    /**
     * Обработка обновления, полученного от Телеграмм
     *
     * @param update обновление, содержащее информацию о сообщении от пользователя
     */
    public void processUpdate(Update update) {
        long chatId = update.message().chat().id();
        String message = update.message().text();
        if (message != null) {
            if (!START_COMMAND.equals(message) && !userService.isChatOpened(chatId)) {
                telegramBotController.sendMessage(
                        "Чтобы начать работу, нажмите " + START_COMMAND, chatId
                );
                return;
            }
            if (message.startsWith(COMMAND_PREFIX)) {
                commandBotProcessor.processCommand(message, chatId);
            } else {
                messageBotProcessor.processMessage(message, chatId);
            }
        }

    }
}
