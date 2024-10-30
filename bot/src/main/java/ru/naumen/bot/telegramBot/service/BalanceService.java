package ru.naumen.bot.telegramBot.service;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Service;
import ru.naumen.bot.data.dao.BalanceDao;

/**
 * Сервис BalanceService предоставляет методы для работы с балансом.
 */
@Service
public class BalanceService {

    /**
     * Dao для работы с балансом.
     */
    private final BalanceDao balanceDao;

    /**
     * Конструктор BalanceService. Реализует сервис с объектами DAO.
     *
     * @param balanceDao DAO для работы с балансом.
     */
    public BalanceService(BalanceDao balanceDao) {
        this.balanceDao = balanceDao;
    }

    /**
     * Возвращает баланс пользователя на основе информации из обновления Telegram.
     *
     * @return текущий баланс пользователя.
     */
    public Double getBalance(Update update) {
        return balanceDao.getBalance(update.message().chat().id());
    }
}
