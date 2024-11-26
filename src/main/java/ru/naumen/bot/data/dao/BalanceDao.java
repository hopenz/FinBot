package ru.naumen.bot.data.dao;

import ru.naumen.bot.exception.DaoException;

/**
 * Интерфейс BalanceDao предоставляет методы работы с балансом пользователя.
 */
public interface BalanceDao {

    /**
     * Устанавливает баланс пользователя для указанного идентификатора.
     *
     * @param chatId     идентификатор чата, в котором необходимо установить баланс
     * @param newBalance сумма нового баланса
     */
    void setBalance(long chatId, double newBalance) throws DaoException;

    /**
     * Возвращает баланс пользователя для указанного идентификатора
     *
     * @param chatId идентификатор чата, из которого необходимо вернуть баланс
     */
    Double getBalance(long chatId) throws DaoException;

    /**
     * Удаляет баланс пользователя для указанного идентификатора
     *
     * @param chatId идентификатор чата, в котором необходимо удалить баланс
     */
    void removeBalance(long chatId) throws DaoException;
}
