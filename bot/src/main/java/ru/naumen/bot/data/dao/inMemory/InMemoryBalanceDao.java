package ru.naumen.bot.data.dao.inMemory;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.dao.BalanceDao;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс, реализующий интерфейс BalanceDao и предоставляющий функционал
 * для установки и вывода баланса.
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

    /**
     * Устанавливает баланс пользователя для указанного идентификатора.
     *
     * @param chatId     идентификатор чата, в котором необходимо установить баланс
     * @param newBalance сумма нового баланса
     */
    @Override
    public void setBalance(long chatId, double newBalance) {
        usersBalance.put(chatId, newBalance);
    }

    /**
     * Получает баланс пользователя для указанного идентификатора.
     *
     * @param chatId идентификатор чата, из которого необходимо вернуть баланс
     * @return текущий баланс пользователя
     */
    @Override
    public Double getBalance(long chatId) {
        return usersBalance.getOrDefault(chatId, 0.0);
    }
}
