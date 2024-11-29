package ru.naumen.bot.data.dao.inMemory;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.data.entity.Expense;

import java.time.LocalDate;
import java.util.List;

/**
 * Тесты для класса {@link InMemoryExpenseDao}, проверяющие функциональность управления расходами.
 */
public class InMemoryExpenseDaoTest {

    /**
     * Тестируемый объект {@link InMemoryExpenseDao}, который проверяется в данном тестовом классе.
     */
    private InMemoryExpenseDao expenseDao;

    /**
     * Идентификатор чата, в котором происходит тестирование.
     */
    private final long chatId = 12345L;

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
        Assertions.assertThat(expenseDao.getExpenses(chatId)).isNull();
    }

    /**
     * Тест для проверки, что метод {@link InMemoryExpenseDao#createUserList(long)} инициализирует
     * пустой список расходов для нового чата.
     */
    @Test
    void testCreateUserListInitializesEmptyExpenseList() {
        expenseDao.createUserList(chatId);

        List<Expense> expenses = expenseDao.getExpenses(chatId);
        Assertions.assertThat(expenses).isNotNull();
        Assertions.assertThat(expenses).isEmpty();
    }

    /**
     * Тест для проверки добавления расхода с помощью метода {@link InMemoryExpenseDao#addExpense(long, Expense)}.
     */
    @Test
    void testAddAndGetExpense() {
        Expense expense = new Expense("мяу", 15.0, LocalDate.now());
        expenseDao.createUserList(chatId);

        expenseDao.addExpense(chatId, expense);

        Assertions.assertThat(expenseDao.getExpenses(chatId)).hasSize(1);
        Assertions.assertThat(expenseDao.getExpenses(chatId)).contains(expense);
    }

    /**
     * Тест для проверки добавления расходов с помощью метода {@link InMemoryExpenseDao#addExpenses(long, List)}.
     */
    @Test
    void testAddAndGetExpenses() {
        List<Expense> expenses = List.of(
                new Expense("Расход 1", 15.0, LocalDate.now()),
                new Expense("Расход 2", 16.0, LocalDate.now())
        );

        expenseDao.createUserList(chatId);
        expenseDao.addExpenses(chatId, expenses);

        Assertions.assertThat(expenseDao.getExpenses(chatId)).hasSize(2);
        Assertions.assertThat(expenseDao.getExpenses(chatId)).containsAll(expenses);
    }

    /**
     * Тест для проверки удаления расходов с помощью метода {@link InMemoryExpenseDao#removeExpenses(long)}.
     */
    @Test
    void testRemoveExpenses() {
        List<Expense> expenses = List.of(
                new Expense("Расход 1", 15.0, LocalDate.now()),
                new Expense("Расход 2", 16.0, LocalDate.now())
        );

        expenseDao.createUserList(chatId);
        expenseDao.addExpenses(chatId, expenses);
        expenseDao.removeExpenses(chatId);

        Assertions.assertThat(expenseDao.getExpenses(chatId)).isEmpty();
    }


}