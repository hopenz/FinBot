package ru.naumen.bot.handler.command.impl;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.enums.ChatState;
import ru.naumen.bot.handler.command.CommandHandler;
import ru.naumen.bot.interaction.CommandData;
import ru.naumen.bot.service.UserService;

import java.util.List;

/**
 * Обработчик команды для вывода справки по всем доступным командам
 */
@Component
public class HelpCommandHandler implements CommandHandler {

    /**
     * Сервис для работы с пользователями
     */
    private final UserService userService;

    /**
     * Конструктор для инициализации обработчика.
     *
     * @param userService сервис для работы с пользователями
     */
    public HelpCommandHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getCommand() {
        return CommandData.HELP_COMMAND.getReadableName();
    }

    @Override
    public List<AnswerMessage> handleCommand(String message, long chatId) {
        userService.setUserState(chatId, ChatState.NOTHING_WAITING);
        CommandData[] arrayOfCommand = CommandData.values();
        StringBuilder stringHelp = new StringBuilder("Справка по всем командам: \n");
        for (CommandData command : arrayOfCommand) {
            stringHelp.append(command.getReadableName()).append(" - ")
                    .append(command.getDescription()).append("\n");
        }
        stringHelp.append("\nЧтобы добавить доход введите:\n+ <сумма> <описание>\n");
        stringHelp.append("\nЧтобы добавить расход введите:\n- <сумма> <описание>");
        return List.of(new AnswerMessage(stringHelp.toString(), chatId));
    }
}
