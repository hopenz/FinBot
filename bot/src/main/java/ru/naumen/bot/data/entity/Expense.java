package ru.naumen.bot.data.entity;

import java.time.LocalDate;

/**
 * Запись Expense представляет расход пользователя.
 * Содержит описание, сумму и дату расхода.
 *
 * @param description описание расхода.
 * @param amount сумма расхода.
 * @param date дата совершения расхода.
 */
public record Expense(String description, Double amount, LocalDate date) {
}
