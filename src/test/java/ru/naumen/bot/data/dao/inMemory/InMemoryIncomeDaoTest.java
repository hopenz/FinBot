package ru.naumen.bot.data.dao.inMemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.data.entity.Income;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryIncomeDaoTest {


    private InMemoryIncomeDao incomeDao;

    @BeforeEach
    void setUp() {
        incomeDao = new InMemoryIncomeDao();
    }

    @Test
    void testGetIncomesReturnsNullForNewChat() {
        long chatId = 12345L;

        assertThat(incomeDao.getIncomes(chatId)).isNull();
    }

    @Test
    void testCreateUserListInitializesEmptyIncomeList() {
        long chatId = 12345L;

        incomeDao.createUserList(chatId);

        List<Income> incomes = incomeDao.getIncomes(chatId);
        assertThat(incomes).isNotNull();
        assertThat(incomes).isEmpty();
    }

    @Test
    void testAddIncome() {
        long chatId = 12345L;
        Income income = new Income("мяу", 15.0, LocalDate.now());
        incomeDao.createUserList(chatId);

        incomeDao.addIncome(chatId, income);

        assertThat(incomeDao.getIncomes(chatId)).hasSize(1);
        assertThat(incomeDao.getIncomes(chatId)).contains(income);
    }
}