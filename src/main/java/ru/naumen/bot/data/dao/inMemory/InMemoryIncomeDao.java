package ru.naumen.bot.data.dao.inMemory;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.dao.IncomeDao;
import ru.naumen.bot.data.entity.Income;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс, предоставляющий функционал для управления данными о доходах пользователей в памяти.
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

    @Override
    public List<Income> getIncomes(long chatId) {
        return usersIncomes.get(chatId);
    }

    @Override
    public void addIncome(long chatId, Income newIncome) {
        usersIncomes.get(chatId).add(newIncome);
    }

    @Override
    public void addIncomes(long chatId, List<Income> incomes) {
        usersIncomes.get(chatId).addAll(incomes);
    }

    @Override
    public void removeIncomes(long chatId) {
        usersIncomes.remove(chatId);
    }

    /**
     * Создает новый список доходов для указанного идентификатора чата.
     *
     * @param chatId идентификатор чата, для которого необходимо создать список доходов.
     */
    public void createUserList(long chatId) {
        usersIncomes.put(chatId, new ArrayList<>());
    }
}
