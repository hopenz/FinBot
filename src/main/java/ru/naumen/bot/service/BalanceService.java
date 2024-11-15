package ru.naumen.bot.service;

import org.springframework.stereotype.Service;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.DaoProvider;

/**
 * Сервис BalanceService предоставляет методы для работы с балансом.
 */
@Service
public class BalanceService {

    /**
     * Класс, предоставляющий доступ к DAO-объектам для работы с данными пользователей.
     */
    private final DaoProvider daoProvider;

    /**
     * Конструктор класса BalanceService.
     *
     * @param daoProvider объект, предоставляющий доступ к DAO-объектам для работы с данными пользователей.
     */
    public BalanceService(DaoProvider daoProvider) {
        this.daoProvider = daoProvider;
    }

    /**
     * Возвращает баланс пользователя на основе информации chatId пользователя.
     *
     * @return текущий баланс пользователя.
     */
    public Double getBalance(long chatId) {
        BalanceDao balanceDao = daoProvider.getBalanceDaoForUser(chatId);
        return balanceDao.getBalance(chatId);
    }

    /**
     * Устанавливает баланс пользователя на основе информации chatId пользователя.
     *
     * @param chatId  идентификатор чата
     * @param balance сумма установленного баланса
     */
    public void setBalance(long chatId, Double balance) {
        BalanceDao balanceDao = daoProvider.getBalanceDaoForUser(chatId);
        balanceDao.setBalance(chatId, balance);
    }

    /**
     * Удаляет баланс пользователя на основе информации chatId пользователя.
     *
     * @param chatId идентификатор чата
     */
    public void removeBalance(long chatId) {
        BalanceDao balanceDao = daoProvider.getBalanceDaoForUser(chatId);
        balanceDao.removeBalance(chatId);
    }
}
