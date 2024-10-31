package ru.naumen.bot.data.dao.inMemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.data.entity.Expense;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для класса {@link InMemoryExpenseDao}, проверяющие функциональность управления расходами.
 */
public class InMemoryExpenseDaoTest {

    /**
     * Тестируемый объект {@link InMemoryExpenseDao}, который проверяется в данном тестовом классе.
     */
    private InMemoryExpenseDao expenseDao;

    /**
     * Инициализация {@link InMemoryExpenseDao} перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        expenseDao = new InMemoryExpenseDao();
    }

    /**
     * Тест для проверки, что метод {@link InMemoryExpenseDao#getExpenses(long)} возвращает null
     * для нового чата, у которого еще нет расходов.
     */
    @Test
    void testGetExpensesReturnsNullForNewChat() {
        long chatId = 12345L;

        assertThat(expenseDao.getExpenses(chatId)).isNull();
    }

    /**
     * Тест для проверки, что метод {@link InMemoryExpenseDao#createUserList(long)} инициализирует
     * пустой список расходов для нового чата.
     */
    @Test
    void testCreateUserListInitializesEmptyExpenseList() {
        long chatId = 12345L;

        expenseDao.createUserList(chatId);

        List<Expense> expenses = expenseDao.getExpenses(chatId);
        assertThat(expenses).isNotNull();
        assertThat(expenses).isEmpty();
    }

    /**
     * Тест для проверки добавления расхода с помощью метода {@link InMemoryExpenseDao#addExpense(long, Expense)}.
     */
    @Test
    void testAddExpense() {
        long chatId = 12345L;
        Expense expense = new Expense("мяу", 15.0, LocalDate.now());
        expenseDao.createUserList(chatId);

        expenseDao.addExpense(chatId, expense);

        assertThat(expenseDao.getExpenses(chatId)).hasSize(1);
        assertThat(expenseDao.getExpenses(chatId)).contains(expense);
    }

}