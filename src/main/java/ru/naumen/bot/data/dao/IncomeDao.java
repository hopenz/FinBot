package ru.naumen.bot.data.dao;

import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.exception.DaoException;

import java.util.List;

/**
 * Интерфейс IncomeDao предоставляет методы для работы с данными о доходах.
 */
public interface IncomeDao {

    /**
     * Возвращает список доходов для указанного идентификатора чата.
     *
     * @param chatId идентификатор чата, для которого нужно получить доходы.
     * @return список объектов {@link Income}, представляющих доходы пользователя.
     * @throws DaoException если возникает ошибка, связанная со взаимодействием с БД.
     */
    List<Income> getIncomes(long chatId) throws DaoException;

    /**
     * Добавляет доход для указанного идентификатора чата.
     *
     * @param chatId    идентификатор чата, для которого нужно добавить расход.
     * @param newIncome новый доход.
     * @throws DaoException если возникает ошибка, связанная со взаимодействием с БД.
     */
    void addIncome(long chatId, Income newIncome) throws DaoException;

    /**
     * Добавляет доходы для указанного идентификатора чата.
     *
     * @param chatId  идентификатор чата, для которого нужно добавить доходы.
     * @param incomes список доходов.
     * @throws DaoException если возникает ошибка, связанная со взаимодействием с БД.
     */
    void addIncomes(long chatId, List<Income> incomes) throws DaoException;

    /**
     * Удаляет доходы для указанного идентификатора чата.
     *
     * @param chatId идентификатор чата, для которого нужно удалить доходы.
     * @throws DaoException если возникает ошибка, связанная со взаимодействием с БД.
     */
    void removeIncomes(long chatId) throws DaoException;
}
