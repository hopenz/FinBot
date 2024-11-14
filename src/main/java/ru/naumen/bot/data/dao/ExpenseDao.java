package ru.naumen.bot.data.dao;


import ru.naumen.bot.data.entity.Expense;

import java.util.List;

/**
 * Интерфейс ExpenseDao предоставляет методы для работы с данными о расходах.
 * Он включает функционал для получения расходов пользователя по идентификатору чата.
 */
public interface ExpenseDao {

    /**
     * Получает список расходов для указанного идентификатора чата.
     *
     * @param chatId идентификатор чата, для которого нужно получить расходы.
     * @return список объектов {@link Expense}, представляющих расходы пользователя.
     */
    List<Expense> getExpenses(long chatId);

    /**
     * Добавляет расход для указанного идентификатора чата.
     *
     * @param chatId     идентификатор чата, для которого нужно добавить расход.
     * @param newExpense новый расход.
     */
    void addExpense(long chatId, Expense newExpense);
}
