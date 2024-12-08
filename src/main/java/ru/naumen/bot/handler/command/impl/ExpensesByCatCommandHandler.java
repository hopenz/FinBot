package ru.naumen.bot.handler.command.impl;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.handler.command.CommandHandler;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.interaction.keyboards.CategoriesKeyboard;
import ru.naumen.bot.service.UserService;

import java.util.List;

/**
 * Обработчик команды для вывода расходов по категории
 */
@Component
public class ExpensesByCatCommandHandler implements CommandHandler {

    /**
     * Количество кнопок на одной строчке в клавиатуре
     */
    private static final int COUNT_BUTTONS_AT_ROW = 3;

    /**
     * Сервис для взаимодействия с данными пользователя
     */
    private final UserService userService;

    /**
     * Класс для формирования клавиатуры с категориями.
     */
    private final CategoriesKeyboard categoriesKeyboard;

    /**
     * Конструктор для инициализации обработчика.
     *
     * @param userService        сервис для работы с пользователями
     * @param categoriesKeyboard утилита для формирования кнопок с категориями
     */
    public ExpensesByCatCommandHandler(UserService userService, CategoriesKeyboard categoriesKeyboard) {
        this.userService = userService;
        this.categoriesKeyboard = categoriesKeyboard;
    }

    @Override
    public String getCommand() {
        return Commands.EXPENSES_BY_CAT.getCommand();
    }

    @Override
    public List<AnswerMessage> handleCommand(String message, long chatId) throws DaoException {
        userService.setUserState(chatId, ChatState.WAITING_EXPENSE_CATEGORY_FOR_OUTPUT);
        List<List<String>> keyboardButtons = categoriesKeyboard.getCategoriesInGroups(COUNT_BUTTONS_AT_ROW);
        return List.of(new AnswerMessage(
                "Выберите категорию для просмотра расходов", chatId, keyboardButtons));
    }
}
