package ru.naumen.bot.data.dao;

import ru.naumen.bot.data.entity.Income;

import java.util.List;

/**
 * Интерфейс IncomeDao предоставляет методы для работы с данными о доходах.
 * Он включает функционал для получения доходов пользователя по идентификатору чата.
 */
public interface IncomeDao {

    /**
     * Получает список доходов для указанного идентификатора чата.
     *
     * @param chatId идентификатор чата, для которого нужно получить доходы.
     * @return список объектов {@link Income}, представляющих доходы пользователя.
     */
    List<Income> getIncomes(long chatId);
}
