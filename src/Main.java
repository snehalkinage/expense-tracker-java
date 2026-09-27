import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ExpenseManager manager = new ExpenseManager(new FileStorage("expenses.csv"));
    private static final SummaryGenerator summaryGenerator = new SummaryGenerator();
    private static final BudgetManager budgetManager = new BudgetManager("budgets.csv");

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> addExpense();
                case "2" -> viewAllExpenses();
                case "3" -> monthlySummary();
                case "4" -> categoryReport();
                case "5" -> editExpense();
                case "6" -> deleteExpense();
                case "7" -> setBudget();
                case "8" -> viewBudgets();
                case "9" -> running = false;
                default -> System.out.println("Invalid option, please try again.");
            }
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println("\n=== Expense Tracker ===");
        System.out.println("1. Add Expense");
        System.out.println("2. View All Expenses");
        System.out.println("3. Monthly Summary");
        System.out.println("4. Category Report");
        System.out.println("5. Edit Expense");
        System.out.println("6. Delete Expense");
        System.out.println("7. Set Category Budget");
        System.out.println("8. View Budgets");
        System.out.println("9. Exit");
        System.out.print("Choose an option: ");
    }

    private static void addExpense() {
        LocalDate date = promptDate("Enter date (YYYY-MM-DD, blank for today): ", true);
        double amount = promptAmount("Enter amount: ");
        System.out.print("Enter category (e.g., Food, Transport, Rent, Entertainment, Other): ");
        String category = scanner.nextLine().trim();
        if (category.isBlank()) category = "Other";
        System.out.print("Enter note (optional): ");
        String note = scanner.nextLine().trim();

        Expense e = manager.addExpense(date, amount, category, note);
        System.out.println("Added: " + e);
    }

    private static void viewAllExpenses() {
        List<Expense> all = manager.getAllSortedByDate();
        if (all.isEmpty()) {
            System.out.println("\nNo expenses recorded yet.");
            return;
        }
        System.out.println("\nDate       | Category     | Amount     | Note");
        System.out.println("-----------------------------------------------------");
        for (Expense e : all) {
            System.out.println(e);
        }
    }

    private static void monthlySummary() {
        int[] my = promptMonthYear();
        List<Expense> monthExpenses = manager.getForMonth(my[0], my[1]);
        Map<String, Double> budgets = budgetManager.getAllBudgets();
        summaryGenerator.printMonthlySummary(monthExpenses, my[0], my[1], budgets);
    }

    private static void categoryReport() {
        summaryGenerator.printCategoryReport(manager.getAll());
    }

    private static void editExpense() {
        int id = promptInt("Enter the ID of the expense to edit: ");
        Optional<Expense> existing = manager.findById(id);
        if (existing.isEmpty()) {
            System.out.println("No expense found with ID " + id);
            return;
        }
        System.out.println("Editing: " + existing.get());
        System.out.println("Leave a field blank to keep it unchanged.");

        LocalDate newDate = promptDate("New date (YYYY-MM-DD): ", false);
        System.out.print("New amount: ");
        String amountStr = scanner.nextLine().trim();
        Double newAmount = amountStr.isBlank() ? null : Double.parseDouble(amountStr);
        System.out.print("New category: ");
        String newCategory = scanner.nextLine().trim();
        System.out.print("New note: ");
        String newNoteInput = scanner.nextLine();
        String newNote = newNoteInput.isBlank() ? null : newNoteInput;

        boolean updated = manager.editExpense(id, newDate, newAmount, newCategory, newNote);
        System.out.println(updated ? "Expense updated." : "Update failed.");
    }

    private static void deleteExpense() {
        int id = promptInt("Enter the ID of the expense to delete: ");
        boolean removed = manager.deleteExpense(id);
        System.out.println(removed ? "Expense deleted." : "No expense found with ID " + id);
    }

    private static void setBudget() {
        System.out.print("Enter category to set a budget for: ");
        String category = scanner.nextLine().trim();
        if (category.isBlank()) {
            System.out.println("Category cannot be blank.");
            return;
        }
        double limit = promptAmount("Enter monthly budget limit for " + category + ": ");
        budgetManager.setBudget(category, limit);
        System.out.printf("Budget set: %s -> $%.2f per month%n", category, limit);
    }

    private static void viewBudgets() {
        Map<String, Double> budgets = budgetManager.getAllBudgets();
        if (budgets.isEmpty()) {
            System.out.println("\nNo budgets set yet.");
            return;
        }
        System.out.println("\n--- Category Budgets (Monthly) ---");
        for (Map.Entry<String, Double> entry : budgets.entrySet()) {
            System.out.printf("  %-14s $%.2f%n", entry.getKey() + ":", entry.getValue());
        }
    }

    // ---- input helpers ----

    private static LocalDate promptDate(String prompt, boolean allowBlankForToday) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isBlank()) {
                return allowBlankForToday ? LocalDate.now() : null;
            }
            try {
                return LocalDate.parse(input);
            } catch (DateTimeException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }

    private static double promptAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double amount = Double.parseDouble(input);
                if (amount <= 0) {
                    System.out.println("Amount must be positive.");
                    continue;
                }
                return amount;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static int promptInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private static int[] promptMonthYear() {
        while (true) {
            System.out.print("Enter month and year (MM/YYYY): ");
            String input = scanner.nextLine().trim();
            try {
                String[] parts = input.split("/");
                int month = Integer.parseInt(parts[0]);
                int year = Integer.parseInt(parts[1]);
                if (month < 1 || month > 12) throw new NumberFormatException();
                return new int[]{month, year};
            } catch (Exception e) {
                System.out.println("Invalid format. Please use MM/YYYY, e.g., 09/2026.");
            }
        }
    }
}