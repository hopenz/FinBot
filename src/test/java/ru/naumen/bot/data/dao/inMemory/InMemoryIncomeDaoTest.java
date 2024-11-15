package ru.naumen.bot.data.dao.inMemory;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.data.entity.Income;

import java.time.LocalDate;
import java.util.List;

/**
 * Тесты для класса {@link InMemoryIncomeDao}, проверяющие функциональность управления доходами.
 */
public class InMemoryIncomeDaoTest {

    /**
     * Тестируемый объект {@link InMemoryIncomeDao}, который проверяется в данном тестовом классе.
     */
    private InMemoryIncomeDao incomeDao;

    /**
     * Идентификатор чата, в котором происходит тестирование.
     */
    private final long chatId = 12345L;


    /**
     * Инициализация {@link InMemoryIncomeDao} перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        incomeDao = new InMemoryIncomeDao();
    }

    /**
     * Тест для проверки, что метод {@link InMemoryIncomeDao#getIncomes(long)} возвращает null
     * для нового чата, у которого еще нет доходов.
     */
    @Test
    void testGetIncomesReturnsNullForNewChat() {
        Assertions.assertThat(incomeDao.getIncomes(chatId)).isNull();
    }

    /**
     * Тест для проверки, что метод {@link InMemoryIncomeDao#createUserList(long)} инициализирует
     * пустой список доходов для нового чата.
     */
    @Test
    void testCreateUserListInitializesEmptyIncomeList() {
        incomeDao.createUserList(chatId);

        List<Income> incomes = incomeDao.getIncomes(chatId);
        Assertions.assertThat(incomes).isNotNull();
        Assertions.assertThat(incomes).isEmpty();
    }

    /**
     * Тест для проверки добавления дохода с помощью метода {@link InMemoryIncomeDao#addIncome(long, Income)}.
     */
    @Test
    void testAddAndGetIncome() {
        Income income = new Income("мяу", 15.0, LocalDate.now());
        incomeDao.createUserList(chatId);

        incomeDao.addIncome(chatId, income);

        Assertions.assertThat(incomeDao.getIncomes(chatId)).hasSize(1);
        Assertions.assertThat(incomeDao.getIncomes(chatId)).contains(income);
    }

    /**
     * Тест для проверки добавления доходов с помощью метода {@link InMemoryIncomeDao#addIncomes(long, List)}.
     */
    @Test
    void testAddAndGetIncomes() {
        List<Income> incomes = List.of(
                new Income("Доход 1", 15.0, LocalDate.now()),
                new Income("Доход 2", 16.0, LocalDate.now())
        );
        incomeDao.createUserList(chatId);

        incomeDao.addIncomes(chatId, incomes);

        Assertions.assertThat(incomeDao.getIncomes(chatId)).hasSize(2);
        Assertions.assertThat(incomeDao.getIncomes(chatId)).containsAll(incomes);
    }

    /**
     * Тест для проверки удаления доходов с помощью метода {@link InMemoryIncomeDao#removeIncomes(long)}.
     */
    @Test
    void testRemoveIncomes() {
        List<Income> incomes = List.of(
                new Income("Доход 1", 15.0, LocalDate.now()),
                new Income("Доход 2", 16.0, LocalDate.now())
        );

        incomeDao.createUserList(chatId);
        incomeDao.addIncomes(chatId, incomes);
        incomeDao.removeIncomes(chatId);

        Assertions.assertThat(incomeDao.getIncomes(chatId)).isNull();
    }
}