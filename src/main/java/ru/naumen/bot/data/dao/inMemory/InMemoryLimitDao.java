package ru.naumen.bot.data.dao.inMemory;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.dao.LimitDao;
import ru.naumen.bot.data.entity.Limit;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для управления лимитами пользователей в памяти.
 */
@Component
public class InMemoryLimitDao implements LimitDao {

    /**
     * Хранилище для лимитов пользователей, где ключом является идентификатор
     * пользователя, а значением - текущий лимит.
     */
    private final Map<Long, Limit> usersLimit = new HashMap<>();

    @Override
    public void setLimit(long chatId, Limit limit) {
        usersLimit.put(chatId, limit);
    }

    @Override
    public void removeLimit(long chatId) {
        usersLimit.remove(chatId);
    }

    @Override
    public Limit getLimit(long chatId) {
        return usersLimit.get(chatId);
    }
}
