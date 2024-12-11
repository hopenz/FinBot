package ru.naumen.bot.data.entity;

import ru.naumen.bot.data.enums.ExpenseCategory;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Объект класса Expense представляет расход пользователя.
 * Содержит описание, сумму, категорию и дату расхода.
 */
public class Expense {

    /**
     * Описание расхода
     */
    private final String description;

    /**
     * Сумма расхода.
     */
    private final Double amount;

    /**
     * Категория расхода.
     */
    private ExpenseCategory category;

    /**
     * Дата совершения расхода.
     */
    private final LocalDate date;

    /**
     * Конструктор для создания нового расхода.
     *
     * @param description Описание расхода
     * @param amount      Сумма расхода
     * @param category    Категория расхода
     * @param date        Дата совершения расхода
     */
    public Expense(String description, Double amount, ExpenseCategory category, LocalDate date) {
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    /**
     * Возвращение описание расхода.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Возвращение суммы расхода.
     */
    public Double getAmount() {
        return amount;
    }

    /**
     * Возвращение категории расхода.
     */
    public ExpenseCategory getCategory() {
        return category;
    }

    /**
     * Возвращение даты совершения расхода.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Установка новую категорию расхода
     */
    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Expense expense = (Expense) o;
        return Objects.equals(description, expense.description)
                && Objects.equals(amount, expense.amount)
                && category == expense.category
                && Objects.equals(date, expense.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, amount, category, date);
    }
}
