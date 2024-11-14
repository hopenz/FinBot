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
        long chatId = 12345L;

        Assertions.assertThat(incomeDao.getIncomes(chatId)).isNull();
    }

    /**
     * Тест для проверки, что метод {@link InMemoryIncomeDao#createUserList(long)} инициализирует
     * пустой список доходов для нового чата.
     */
    @Test
    void testCreateUserListInitializesEmptyIncomeList() {
        long chatId = 12345L;

        incomeDao.createUserList(chatId);

        List<Income> incomes = incomeDao.getIncomes(chatId);
        Assertions.assertThat(incomes).isNotNull();
        Assertions.assertThat(incomes).isEmpty();
    }

    /**
     * Тест для проверки добавления дохода с помощью метода {@link InMemoryIncomeDao#addIncome(long, Income)}.
     */
    @Test
    void testAddIncome() {
        long chatId = 12345L;
        Income income = new Income("мяу", 15.0, LocalDate.now());
        incomeDao.createUserList(chatId);

        incomeDao.addIncome(chatId, income);

        Assertions.assertThat(incomeDao.getIncomes(chatId)).hasSize(1);
        Assertions.assertThat(incomeDao.getIncomes(chatId)).contains(income);
    }
}