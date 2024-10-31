package ru.naumen.bot.telegramBot.processor;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;
import ru.naumen.bot.telegramBot.controller.TelegramBotController;
import ru.naumen.bot.telegramBot.service.UserService;

import static ru.naumen.bot.telegramBot.command.Commands.COMMAND_PREFIX;
import static ru.naumen.bot.telegramBot.command.Commands.START_COMMAND;

@Component
public class UpdateBotProcessor {

    private final MessageBotProcessor messageBotProcessor;

    private final CommandBotProcessor commandBotProcessor;

    private final UserService userService;

    private final TelegramBotController telegramBotController;

    public UpdateBotProcessor(MessageBotProcessor messageBotProcessor, CommandBotProcessor commandBotProcessor,
                              UserService userService, TelegramBotController telegramBotController1) {
        this.messageBotProcessor = messageBotProcessor;
        this.commandBotProcessor = commandBotProcessor;
        this.userService = userService;
        this.telegramBotController = telegramBotController1;
    }

    public void processUpdate(Update update) {
        long chatId = update.message().chat().id();
        String message = update.message().text();
        if(message != null) {
            if (!START_COMMAND.equals(message) && !userService.isChatOpened(chatId)) {
                telegramBotController.sendMessage(
                        "Чтобы начать работу, нажмите " + START_COMMAND, chatId
                );
                return;
            }
            if(message.startsWith(COMMAND_PREFIX)) {
                commandBotProcessor.processCommand(message, chatId);
            } else {
                messageBotProcessor.processMessage(message, chatId);
            }
        }

    }
}
