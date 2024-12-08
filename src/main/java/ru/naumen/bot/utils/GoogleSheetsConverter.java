package ru.naumen.bot.utils;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.ExpenseCategory;
import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.data.entity.Limit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Преобразование объектов в формат Google Sheets и обратно
 */
@Component
public class GoogleSheetsConverter {

    /**
     * Преобразование объекта income в формат Google Sheets
     *
     * @param income доход
     * @return список списков, содержащий данные о доходе в формате таблицы
     */
    public List<List<Object>> incomeToSheetFormat(Income income) {
        return List.of(List.of(income.description(), income.amount(), "'" + income.date().toString()));
    }

    /**
     * Преобразование списка доходов в формат Google Sheets
     *
     * @param incomes список доходов
     * @return список списков, содержащий данные о доходах в формате таблицы
     */
    public List<List<Object>> incomesToSheetFormat(List<Income> incomes) {
        return incomes.stream()
                .map(income -> List.<Object>of(income.description(),
                        income.amount(), "'" + income.date().toString()))
                .collect(Collectors.toList());
    }

    /**
     * Преобразование объекта expense в формат Google Sheets
     *
     * @param expense расход
     * @return список списков, содержащий данные о расходе в формате таблицы
     */
    public List<List<Object>> expenseToSheetFormat(Expense expense) {
        return List.of(List.of(expense.getDescription(), expense.getAmount(),
                expense.getCategory().name(), "'" + expense.getDate().toString()));
    }

    /**
     * Преобразование списка расходов в формат Google Sheets
     *
     * @param expenses список расходов
     * @return список списков, содержащий данные о расходах в формате таблицы
     */
    public List<List<Object>> expensesToSheetFormat(List<Expense> expenses) {
        return expenses.stream()
                .map(expense -> List.<Object>of(expense.getDescription(), expense.getAmount(),
                        expense.getCategory().name(), "'" + expense.getDate().toString()))
                .collect(Collectors.toList());
    }

    /**
     * Преобразует данные из формата таблицы в список объектов Income
     *
     * @param data данные из Google Sheets в формате списка списков
     * @return список объектов Income
     */
    public List<Income> sheetFormatToIncomes(List<List<Object>> data) {
        if (data == null) {
            return List.of();
        }
        List<Income> result = new ArrayList<>(data.size());
        for (List<Object> row : data) {
            result.add(new Income((String) row.get(0), Double.parseDouble((String) row.get(1)),
                    LocalDate.parse((String) row.get(2))));
        }
        return result;
    }

    /**
     * Преобразует данные из формата таблицы в список объектов Expense
     *
     * @param data данные из Google Sheets в формате списка списков
     * @return список объектов Expense
     */
    public List<Expense> sheetFormatToExpenses(List<List<Object>> data) {
        if (data == null) {
            return List.of();
        }
        List<Expense> result = new ArrayList<>(data.size());
        for (List<Object> row : data) {
            result.add(new Expense((String) row.get(0), Double.parseDouble((String) row.get(1)),
                    ExpenseCategory.valueOf((String) row.get(2)), LocalDate.parse((String) row.get(3))));
        }
        return result;
    }

    /**
     * Преобразует значение баланса в формат, подходящий для Google Sheets
     *
     * @param newBalance значение баланса для преобразования
     * @return список списков, содержащий значение баланса в формате таблицы
     */
    public List<List<Object>> doubleToSheetFormat(double newBalance) {
        return List.of(List.of(newBalance));
    }

    /**
     * Преобразует данные из формата таблицы в значение типа Double
     *
     * @param data данные из Google Sheets в формате списка списков
     * @return значение типа Double
     */
    public Double sheetFormatToDouble(List<List<Object>> data) {
        if (data == null) {
            return 0.0;
        }
        return Double.parseDouble((String) data.getFirst().getFirst());
    }

    /**
     * Преобразует строку в формат, подходящий для использования в Google Sheets.
     *
     * @param string строка, которую необходимо преобразовать в формат для таблицы.
     * @return двумерный список, содержащий одну строку с переданным значением строки.
     */
    public List<List<Object>> stringToSheetFormat(String string) {
        return List.of(List.of(string));
    }

    public List<List<Object>> limitToSheetFormat(Limit limit) {
        if (limit == null) {
            return List.of(List.of());
        }
        return List.of(List.of(limit.getDailyLimit(), limit.getDailyExpensesSum()));
    }

    public Limit sheetFormatToLimit(List<List<Object>> data) {
        if (data == null) {
            return null;
        }
        return new Limit(Double.parseDouble((String) data.getFirst().getFirst()),
                Double.parseDouble((String) data.getFirst().get(1)));
    }
}
