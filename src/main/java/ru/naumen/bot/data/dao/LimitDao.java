package ru.naumen.bot.data.dao;

import ru.naumen.bot.data.entity.Limit;
import ru.naumen.bot.exception.DaoException;

/**
 * Интерфейс для работы с лимитами расходов
 */
public interface LimitDao {

    /**
     * Устанавливает лимит расходов для заданного идентификатора чата.
     *
     * @param chatId Идентификатор чата, для которого устанавливается лимит.
     * @param limit  Объект {@link Limit}, представляющий новый лимит расходов.
     */
    void setLimit(long chatId, Limit limit) throws DaoException;

    /**
     * Удаляет лимит расходов для заданного идентификатора чата.
     *
     * @param chatId Идентификатор чата, для которого удаляется лимит.
     */
    void removeLimit(long chatId) throws DaoException;

    /**
     * Возвращает текущий лимит расходов для заданного идентификатора чата.
     *
     * @param chatId Идентификатор чата, для которого запрашивается лимит.
     * @return Объект {@link Limit}, представляющий текущий лимит расходов,
     * или null, если лимит не установлен.
     */
    Limit getLimit(long chatId) throws DaoException;
}
