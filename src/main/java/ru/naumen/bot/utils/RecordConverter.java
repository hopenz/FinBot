package ru.naumen.bot.utils;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.Income;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecordConverter {

    public List<List<Object>> incomeToSheetFormat(Income income){
        return List.of(List.of(income.description(), income.amount(), income.date()));
    }

    public List<List<Object>> incomesToSheetFormat(List<Income> incomes){
        return incomes.stream()
                .map(income -> List.<Object>of(income.description(), income.amount(), income.date()))
                .collect(Collectors.toList());
    }

    public List<List<Object>> expenseToSheetFormat(Expense expense){
        return List.of(List.of(expense.description(), expense.amount(), expense.date()));
    }

    public List<List<Object>> expensesToSheetFormat(List<Expense> expenses){
        return expenses.stream()
                .map(expense -> List.<Object>of(expense.description(), expense.amount(), expense.date()))
                .collect(Collectors.toList());
    }
}
