package ru.naumen.bot.handler.command.impl;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.handler.command.CommandHandler;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.service.BalanceService;
import ru.naumen.bot.service.UserService;

import java.util.List;

/**
 * Обработчик команды для получения текущего баланса пользователя
 */
@Component
public class BalanceCommandHandler implements CommandHandler {

    /**
     * Сервис для работы с балансом
     */
    private final BalanceService balanceService;

    /**
     * Сервис для взаимодействия с данными пользователя.
     */
    private final UserService userService;

    /**
     * Конструктор для инициализации обработчика.
     *
     * @param balanceService сервис для работы с балансом
     * @param userService    сервис для работы с пользователями
     */
    public BalanceCommandHandler(BalanceService balanceService, UserService userService) {
        this.balanceService = balanceService;
        this.userService = userService;
    }

    @Override
    public String getCommand() {
        return Commands.BALANCE_COMMAND.getCommand();
    }

    @Override
    public List<AnswerMessage> handleCommand(String message, long chatId) throws DaoException {
        userService.setUserState(chatId, ChatState.NOTHING_WAITING);
        Double balance = balanceService.getBalance(chatId);
        return List.of(new AnswerMessage("Ваш баланс: " + balance, chatId));
    }
}
