package ru.naumen.bot.data.dao.inMemory;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.dao.BalanceDao;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс, предоставляющий функционал для установки и вывода баланса.
 *
 * <p>Использует HashMap для хранения балансов пользователей, где ключом
 * является идентификатор чата, а значением — текущий баланс.
 */
@Component
public class InMemoryBalanceDao implements BalanceDao {

    /**
     * Хранилище для балансов пользователей, где ключом является идентификатор
     * пользователя, а значением - текущий баланс.
     */
    private final Map<Long, Double> usersBalance = new HashMap<>();

    @Override
    public void setBalance(long chatId, double newBalance) {
        usersBalance.put(chatId, newBalance);
    }

    @Override
    public Double getBalance(long chatId) {
        return usersBalance.getOrDefault(chatId, 0.0);
    }

    @Override
    public void removeBalance(long chatId) {
        usersBalance.remove(chatId);
    }
}