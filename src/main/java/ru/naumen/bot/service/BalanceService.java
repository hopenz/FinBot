package ru.naumen.bot.service;

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
     * Возвращает баланс пользователя на основе информации chatId пользователя.
     *
     * @return текущий баланс пользователя.
     */
    public Double getBalance(long chatId) {
        return balanceDao.getBalance(chatId);
    }
}
