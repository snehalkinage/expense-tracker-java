import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

/**
 * Computes monthly and category-wise summaries from a list of expenses.
 */
public class SummaryGenerator {

    public void printMonthlySummary(List<Expense> monthExpenses, int month, int year) {
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