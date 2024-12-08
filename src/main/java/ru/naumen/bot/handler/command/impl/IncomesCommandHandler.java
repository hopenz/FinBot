package ru.naumen.bot.handler.command.impl;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.handler.command.CommandHandler;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.service.IncomeService;
import ru.naumen.bot.service.UserService;

import java.util.List;

/**
 * Обработчик команды для вывода всех доходов пользователя
 */
@Component
public class IncomesCommandHandler implements CommandHandler {

    /**
     * Сервис для работы с доходами
     */
    private final IncomeService incomeService;

    /**
     * Сервис для работы с пользователями
     */
    private final UserService userService;

    /**
     * Конструктор для инициализации обработчика.
     *
     * @param incomeService сервис для работы с доходами
     * @param userService   сервис для работы с пользователями
     */
    public IncomesCommandHandler(IncomeService incomeService, UserService userService) {
        this.incomeService = incomeService;
        this.userService = userService;
    }

    @Override
    public String getCommand() {
        return Commands.INCOMES_COMMAND.getCommand();
    }

    @Override
    public List<AnswerMessage> handleCommand(String message, long chatId) throws DaoException {
        userService.setUserState(chatId, ChatState.NOTHING_WAITING);
        List<Income> incomeList = incomeService.getIncomes(chatId);
        StringBuilder incomes = new StringBuilder();
        incomes.append("Ваши доходы:\n");
        for (Income income : incomeList) {
            incomes.append(income.amount()).append(" - ").append(income.description()).append("\n");
        }
        return List.of(new AnswerMessage(incomes.toString(), chatId));
    }
}
