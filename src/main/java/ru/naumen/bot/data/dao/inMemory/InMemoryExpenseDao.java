package ru.naumen.bot.data.dao.inMemory;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.dao.ExpenseDao;
import ru.naumen.bot.data.entity.Expense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс, предоставляющий функционал для управления данными о расходах пользователей в памяти.
 *
 * <p>Использует HashMap для хранения расходов пользователей, где ключом
 * является идентификатор чата, а значением — список расходов.
 */
@Component
public class InMemoryExpenseDao implements ExpenseDao {

    /**
     * Хранилище для расходов пользователей, где ключом является идентификатор
     * чата, а значением — список объектов {@link Expense}.
     */
    private final Map<Long, List<Expense>> usersExpenses = new HashMap<>();

    @Override
    public List<Expense> getExpenses(long chatId) {
        return usersExpenses.get(chatId);
    }

    @Override
    public void addExpense(long chatId, Expense newExpense) {
        usersExpenses.get(chatId).add(newExpense);
    }

    @Override
    public void addExpenses(long chatId, List<Expense> expenses) {
        usersExpenses.get(chatId).addAll(expenses);
    }

    @Override
    public void removeExpenses(long chatId) {
        usersExpenses.get(chatId).clear();
    }

    /**
     * Создает новый список расходов для указанного идентификатора чата.
     *
     * @param chatId идентификатор чата, для которого необходимо создать список расходов.
     */
    public void createUserList(long chatId) {
        usersExpenses.put(chatId, new ArrayList<>());
    }
}
