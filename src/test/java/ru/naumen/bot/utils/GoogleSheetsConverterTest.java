package ru.naumen.bot.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.ExpenseCategory;
import ru.naumen.bot.data.entity.Income;

import java.time.LocalDate;
import java.util.List;

/**
 * Тесты для класса GoogleSheetsConverter.
 */
public class GoogleSheetsConverterTest {

    private final GoogleSheetsConverter converter = new GoogleSheetsConverter();

    /**
     * Тест для метода incomeToSheetFormat.
     * Проверяет правильность преобразования объекта Income в формат таблицы.
     */
    @Test
    public void testIncomeToSheetFormat() {
        Income income = new Income("Salary", 5000.0, LocalDate.of(2023, 11, 1));
        List<List<Object>> expected = List.of(List.of("Salary", 5000.0, "'2023-11-01"));

        List<List<Object>> result = converter.incomeToSheetFormat(income);

        Assertions.assertThat(result).isEqualTo(expected);
    }

    /**
     * Тест для метода incomesToSheetFormat.
     * Проверяет правильность преобразования списка объектов Income в формат таблицы.
     */
    @Test
    public void testIncomesToSheetFormat() {
        List<Income> incomes = List.of(
                new Income("Salary", 5000.0, LocalDate.of(2023, 11, 1)),
                new Income("Bonus", 1500.0, LocalDate.of(2023, 11, 5))
        );

        List<List<Object>> expected = List.of(
                List.of("Salary", 5000.0, "'2023-11-01"),
                List.of("Bonus", 1500.0, "'2023-11-05")
        );

        List<List<Object>> result = converter.incomesToSheetFormat(incomes);

        Assertions.assertThat(result).isEqualTo(expected);
    }

    /**
     * Тест для метода expenseToSheetFormat.
     * Проверяет правильность преобразования объекта Expense в формат таблицы.
     */
    @Test
    public void testExpenseToSheetFormat() {
        Expense expense = new Expense("Groceries", 150.0,
                ExpenseCategory.OTHER, LocalDate.of(2023, 11, 3));
        List<List<Object>> expected = List.of(List.of("Groceries", 150.0, "OTHER", "'2023-11-03"));

        List<List<Object>> result = converter.expenseToSheetFormat(expense);

        Assertions.assertThat(result).isEqualTo(expected);
    }

    /**
     * Тест для метода expensesToSheetFormat.
     * Проверяет правильность преобразования списка объектов Expense в формат таблицы.
     */
    @Test
    public void testExpensesToSheetFormat() {
        List<Expense> expenses = List.of(
                new Expense("Groceries", 150.0,
                        ExpenseCategory.TRANSPORT, LocalDate.of(2023, 11, 3)),
                new Expense("Rent", 1000.0,
                        ExpenseCategory.OTHER, LocalDate.of(2023, 11, 1))
        );

        List<List<Object>> expected = List.of(
                List.of("Groceries", 150.0, "TRANSPORT", "'2023-11-03"),
                List.of("Rent", 1000.0, "OTHER", "'2023-11-01")
        );

        List<List<Object>> result = converter.expensesToSheetFormat(expenses);

        Assertions.assertThat(result).isEqualTo(expected);
    }

    /**
     * Тест для метода sheetFormatToIncomes.
     * Проверяет правильность преобразования данных из формата таблицы в список объектов Income.
     */
    @Test
    public void testSheetFormatToIncomes() {
        List<List<Object>> data = List.of(
                List.of("Salary", "5000.0", "2023-11-01"),
                List.of("Bonus", "1500.0", "2023-11-05")
        );

        List<Income> expected = List.of(
                new Income("Salary", 5000.0, LocalDate.of(2023, 11, 1)),
                new Income("Bonus", 1500.0, LocalDate.of(2023, 11, 5))
        );

        List<Income> result = converter.sheetFormatToIncomes(data);

        Assertions.assertThat(result).isEqualTo(expected);
    }

    /**
     * Тест для метода sheetFormatToExpenses.
     * Проверяет правильность преобразования данных из формата таблицы в список объектов Expense.
     */
    @Test
    public void testSheetFormatToExpenses() {
        List<List<Object>> data = List.of(
                List.of("Groceries", "150.0", "OTHER", "2023-11-03"),
                List.of("Rent", "1000.0", "SUPERMARKET", "2023-11-01")
        );

        List<Expense> expected = List.of(
                new Expense("Groceries", 150.0,
                        ExpenseCategory.OTHER, LocalDate.of(2023, 11, 3)),
                new Expense("Rent", 1000.0,
                        ExpenseCategory.SUPERMARKET, LocalDate.of(2023, 11, 1))
        );

        List<Expense> result = converter.sheetFormatToExpenses(data);

        Assertions.assertThat(result).isEqualTo(expected);
    }

    /**
     * Тест для метода doubleToSheetFormat.
     * Проверяет правильность преобразования значения Double в формат таблицы.
     */
    @Test
    public void testDoubleToSheetFormat() {
        double balance = 12345.67;
        List<List<Object>> expected = List.of(List.of(balance));

        List<List<Object>> result = converter.doubleToSheetFormat(balance);

        Assertions.assertThat(result).isEqualTo(expected);
    }

    /**
     * Тест для метода sheetFormatToDouble.
     * Проверяет правильность преобразования данных из формата таблицы в значение типа Double.
     */
    @Test
    public void testSheetFormatToDouble() {
        List<List<Object>> data = List.of(List.of("12345.67"));
        Double expected = 12345.67;

        Double result = converter.sheetFormatToDouble(data);

        Assertions.assertThat(result).isEqualTo(expected);
    }

    /**
     * Тест для метода sheetFormatToDouble при отсутствии баланса в таблице.
     * Проверяет, что возвращается значение 0.0, если входные данные равны null.
     */
    @Test
    public void testSheetFormatToDoubleWithNullOrEmptyData() {
        Assertions.assertThat(converter.sheetFormatToDouble(null)).isEqualTo(0.0);
    }
}