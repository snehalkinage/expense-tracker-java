import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

/**
 * Computes monthly and category-wise summaries from a list of expenses.
 * Optionally checks spending against per-category budget limits and prints warnings.
 */
public class SummaryGenerator {

    public void printMonthlySummary(List<Expense> monthExpenses, int month, int year, Map<String, Double> budgets) {
        String monthName = LocalDate.of(year, month, 1).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        if (monthExpenses.isEmpty()) {
            System.out.println("\nNo expenses found for " + monthName + " " + year + ".");
            return;
        }

        double total = monthExpenses.stream().mapToDouble(Expense::getAmount).sum();
        Map<String, Double> byCategory = new TreeMap<>();
        for (Expense e : monthExpenses) {
            byCategory.merge(e.getCategory(), e.getAmount(), Double::sum);
        }
        Expense highest = Collections.max(monthExpenses, Comparator.comparingDouble(Expense::getAmount));

        System.out.println("\n--- Summary for " + monthName + " " + year + " ---");
        System.out.printf("Total Spent: $%.2f%n", total);
        System.out.println("Transactions: " + monthExpenses.size());
        System.out.println("\nBy Category:");
        for (Map.Entry<String, Double> entry : byCategory.entrySet()) {
            double pct = (entry.getValue() / total) * 100;
            System.out.printf("  %-14s $%-10.2f (%.1f%%)%n", entry.getKey() + ":", entry.getValue(), pct);
        }
        System.out.printf("%nHighest Expense: %s - $%.2f on %s%n",
                highest.getCategory(), highest.getAmount(), highest.getDate());

        printBudgetWarnings(byCategory, budgets);
    }

    /** Overload for callers that don't use budgets (keeps old behavior available). */
    public void printMonthlySummary(List<Expense> monthExpenses, int month, int year) {
        printMonthlySummary(monthExpenses, month, year, Collections.emptyMap());
    }

    private void printBudgetWarnings(Map<String, Double> byCategory, Map<String, Double> budgets) {
        if (budgets == null || budgets.isEmpty()) {
            return;
        }
        List<String> warnings = new ArrayList<>();
        for (Map.Entry<String, Double> budgetEntry : budgets.entrySet()) {
            String category = budgetEntry.getKey();
            double limit = budgetEntry.getValue();
            double spent = byCategory.getOrDefault(category, 0.0);
            if (spent > limit) {
                warnings.add(String.format("  \u26A0 %s: spent $%.2f of $%.2f budget (over by $%.2f)",
                        category, spent, limit, spent - limit));
            }
        }
        if (!warnings.isEmpty()) {
            System.out.println("\nBudget Warnings:");
            warnings.forEach(System.out::println);
        }
    }

    public void printCategoryReport(List<Expense> allExpenses) {
        if (allExpenses.isEmpty()) {
            System.out.println("\nNo expenses recorded yet.");
            return;
        }
        Map<String, Double> byCategory = new TreeMap<>();
        double total = 0;
        for (Expense e : allExpenses) {
            byCategory.merge(e.getCategory(), e.getAmount(), Double::sum);
            total += e.getAmount();
        }
        System.out.println("\n--- Category Report (All Time) ---");
        for (Map.Entry<String, Double> entry : byCategory.entrySet()) {
            double pct = (entry.getValue() / total) * 100;
            System.out.printf("  %-14s $%-10.2f (%.1f%%)%n", entry.getKey() + ":", entry.getValue(), pct);
        }
        System.out.printf("%nGrand Total: $%.2f%n", total);
    }
}