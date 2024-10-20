package ru.naumen.bot.data.dao;

/**
 * Интерфейс BalanceDao предоставляет методы работы с балансом пользователя.
 * Он включает в себя функционал вывода и установки текущего баланса пользователя.
 */
public interface BalanceDao {

    /**
     * Установить баланс пользователя
     *
     * @param chatId     идентификатор чата, в котором необходимо установить баланс
     * @param newBalance сумма нового баланса
     */
    void setBalance(long chatId, double newBalance);

    /**
     * Вернуть баланс пользователя
     *
     * @param chatId идентификатор чата, из которого необходимо вернуть баланс
     * @return баланс double
     */
    Double getBalance(long chatId);
}
