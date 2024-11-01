package ru.naumen.bot.telegramBot.processor;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;

import static ru.naumen.bot.telegramBot.command.Commands.COMMAND_PREFIX;

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
     * Конструктор {@link UpdateBotProcessor}
     *
     * @param messageBotProcessor процессор для обработки сообщений от пользователей
     * @param commandBotProcessor процессор для обработки команд от пользователей
     */
    public UpdateBotProcessor(MessageBotProcessor messageBotProcessor, CommandBotProcessor commandBotProcessor) {
        this.messageBotProcessor = messageBotProcessor;
        this.commandBotProcessor = commandBotProcessor;
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
            if (!commandBotProcessor.isChatActiveOrStarting(message, chatId)) return;
            if (message.startsWith(COMMAND_PREFIX)) {
                commandBotProcessor.processCommand(message, chatId);
            } else {
                messageBotProcessor.processMessage(message, chatId);
            }
        }

    }
}
