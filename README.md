# Expense Tracker (Java)

A console-based Java application for tracking daily expenses, with monthly summaries and category-wise reporting. Built as a mini-project to practice OOP, file I/O, and date/collection handling in core Java.

## Features
- Add expenses with date, amount, category, and an optional note
- View all expenses, sorted by date
- Monthly summary: total spent, category breakdown with percentages, transaction count, and highest expense
- Category report: totals per category across all recorded expenses
- Edit or delete any expense by ID
- Data automatically saved to and loaded from a local CSV file (`expenses.csv`), so nothing is lost between runs

## Tech Stack
- Java 17+ (uses `java.time.LocalDate`, switch expressions, streams)
- No external dependencies — pure core Java

## Project Structure
