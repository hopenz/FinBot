package ru.naumen.bot.handler.command.impl;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.enums.ChatState;
import ru.naumen.bot.handler.command.CommandHandler;
import ru.naumen.bot.interaction.CommandData;
import ru.naumen.bot.interaction.keyboards.TypeDBKeyboard;
import ru.naumen.bot.service.UserService;

import java.util.Arrays;
import java.util.List;

/**
 * Обработчик команды для смены базы данных
 */
@Component
public class ChangeDbCommandHandler implements CommandHandler {

    /**
     * Сервис для взаимодействия с данными пользователя.
     */
    private final UserService userService;

    /**
     * Конструктор для инициализации обработчика.
     *
     * @param userService сервис для работы с пользователями
     */
    public ChangeDbCommandHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getCommand() {
        return CommandData.CHANGE_DB_COMMAND.getReadableName();
    }

    @Override
    public List<AnswerMessage> handleCommand(String message, long chatId) {
        userService.setUserState(chatId, ChatState.WAITING_FOR_TYPE_DB_FOR_CHANGE_DB);
        List<String> keyboardButtons = Arrays.stream(TypeDBKeyboard.values())
                .map(TypeDBKeyboard::getData)
                .toList();
        return List.of(new AnswerMessage("Выберите базу данных", chatId, List.of(keyboardButtons)));
    }
}
