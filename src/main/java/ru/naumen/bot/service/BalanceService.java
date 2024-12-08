package ru.naumen.bot.service;

import org.springframework.stereotype.Service;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.provider.BalanceDaoProvider;
import ru.naumen.bot.exception.DaoException;

/**
 * Сервис BalanceService предоставляет методы для работы с балансом.
 */
@Service
public class BalanceService {

    /**
     * Провайдер DAO для работы с балансом пользователей.
     */
    private final BalanceDaoProvider balanceDaoProvider;

    /**
     * Конструктор класса BalanceService.
     *
     * @param balanceDaoProvider Провайдер DAO для работы с балансом пользователей..
     */
    public BalanceService(BalanceDaoProvider balanceDaoProvider) {
        this.balanceDaoProvider = balanceDaoProvider;
    }

    /**
     * Возвращает баланс пользователя на основе информации chatId пользователя.
     *
     * @return текущий баланс пользователя.
     */
    public Double getBalance(long chatId) throws DaoException {
        BalanceDao balanceDao = balanceDaoProvider.getBalanceDaoForUser(chatId);
        return balanceDao.getBalance(chatId);
    }

    /**
     * Устанавливает баланс пользователя на основе информации chatId пользователя.
     *
     * @param chatId  идентификатор чата
     * @param balance сумма установленного баланса
     */
    public void setBalance(long chatId, Double balance) throws DaoException {
        BalanceDao balanceDao = balanceDaoProvider.getBalanceDaoForUser(chatId);
        balanceDao.setBalance(chatId, balance);
    }

    /**
     * Удаляет баланс пользователя на основе информации chatId пользователя.
     *
     * @param chatId идентификатор чата
     */
    public void removeBalance(long chatId) throws DaoException {
        BalanceDao balanceDao = balanceDaoProvider.getBalanceDaoForUser(chatId);
        balanceDao.removeBalance(chatId);
    }
}
