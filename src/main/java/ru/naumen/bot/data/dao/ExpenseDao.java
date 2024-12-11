package ru.naumen.bot.data.dao;

import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.enums.ExpenseCategory;
import ru.naumen.bot.exception.DaoException;

import java.util.List;

/**
 * Интерфейс ExpenseDao предоставляет методы для работы с данными о расходах.
 */
public interface ExpenseDao {

    /**
     * Возвращает список расходов для указанного идентификатора чата.
     *
     * @param chatId идентификатор чата, для которого нужно получить расходы.
     * @return список объектов {@link Expense}, представляющих расходы пользователя.
     * @throws DaoException если возникает ошибка, связанная со взаимодействием с БД.
     */
    List<Expense> getExpenses(long chatId) throws DaoException;

    /**
     * Добавляет расход для указанного идентификатора чата.
     *
     * @param chatId     идентификатор чата, для которого нужно добавить расход.
     * @param newExpense новый расход.
     * @throws DaoException если возникает ошибка, связанная со взаимодействием с БД.
     */
    void addExpense(long chatId, Expense newExpense) throws DaoException;

    /**
     * Добавляет расходы для указанного идентификатора чата.
     *
     * @param chatId   идентификатор чата, для которого нужно добавить расходы.
     * @param expenses список расходов.
     * @throws DaoException если возникает ошибка, связанная со взаимодействием с БД.
     */
    void addExpenses(long chatId, List<Expense> expenses) throws DaoException;

    /**
     * Удаляет расходы для указанного идентификатора чата.
     *
     * @param chatId идентификатор чата, для которого нужно удалить расходы.
     * @throws DaoException если возникает ошибка, связанная со взаимодействием с БД.
     */
    void removeExpenses(long chatId) throws DaoException;

    /**
     * Меняет категорию последнего расхода для указанного идентификатора чата.
     *
     * @param chatId      идентификатор чата, для которого нужно изменить категорию расхода.
     * @param newCategory новая категория расхода.
     * @throws DaoException если возникает ошибка, связанная со взаимодействием с БД.
     */
    void setLastExpenseCategory(long chatId, ExpenseCategory newCategory) throws DaoException;
}
