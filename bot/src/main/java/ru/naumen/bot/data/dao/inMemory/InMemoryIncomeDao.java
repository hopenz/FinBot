package ru.naumen.bot.data.dao.inMemory;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.dao.IncomeDao;
import ru.naumen.bot.data.entity.Income;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс InMemoryIncomeDao реализует интерфейс IncomeDao и предоставляет
 * функционал для управления данными о доходах пользователей в памяти.
 *
 * <p>Использует HashMap для хранения доходов пользователей, где ключом
 * является идентификатор чата, а значением — список доходов.
 */
@Component
public class InMemoryIncomeDao implements IncomeDao {

    /**
     * Хранилище для доходов пользователей, где ключом является идентификатор
     * чата, а значением — список объектов {@link Income}.
     */
    private final Map<Long, List<Income>> usersIncomes = new HashMap<>();

    /**
     * Получает список доходов для указанного идентификатора чата.
     *
     * @param chatId идентификатор чата, для которого нужно получить доходы.
     * @return список объектов {@link Income}, представляющих доходы пользователя.
     */
    @Override
    public List<Income> getIncomes(long chatId) {
        return usersIncomes.get(chatId);
    }

    /**
     * Добавляет доход для указанного идентификатора чата.
     *
     * @param chatId    идентификатор чата, для которого нужно добавить расход.
     * @param newIncome новый доход.
     */
    @Override
    public void addIncome(long chatId, Income newIncome) {
        usersIncomes.get(chatId).add(newIncome);
    }

    /**
     * Создает новый список доходов для указанного идентификатора чата.
     * Этот метод используется для инициализации хранилища доходов,
     * когда пользователь начинает с пустым списком доходов.
     *
     * @param chatId идентификатор чата, для которого необходимо создать список доходов.
     */
    public void createUserList(long chatId) {
        usersIncomes.put(chatId, new ArrayList<>());
    }
}
