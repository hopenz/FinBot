package ru.naumen.bot.handler.command.impl;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.handler.command.CommandHandler;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.interaction.keyboards.TypeDBKeyboard;
import ru.naumen.bot.service.UserService;

import java.util.Arrays;
import java.util.List;

/**
 * Обработчик команды для начала работы с ботом
 */
@Component
public class StartCommandHandler implements CommandHandler {

    /**
     * Сервис для работы с пользователями
     */
    private final UserService userService;

    /**
     * Конструктор для инициализации обработчика.
     *
     * @param userService сервис для работы с пользователями
     */
    public StartCommandHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getCommand() {
        return Commands.START_COMMAND.getCommand();
    }

    @Override
    public List<AnswerMessage> handleCommand(String message, long chatId) {
        if (userService.isChatOpened(chatId)) {
            userService.setUserState(chatId, ChatState.NOTHING_WAITING);
            return List.of(new AnswerMessage(
                    "Ещё раз здравствуйте, чтобы ознакомиться с командами - напишите " +
                            Commands.HELP_COMMAND.getCommand(), chatId));
        } else {
            userService.openChat(chatId);
            List<String> keyboardButtons = Arrays.stream(TypeDBKeyboard.values())
                    .map(TypeDBKeyboard::getData)
                    .toList();
            return List.of(new AnswerMessage(
                    "Здравствуйте! Как вы хотите хранить данные?", chatId, List.of(keyboardButtons)));
        }
    }
}
