import java.time.LocalDate;
public class Expense {
    private int id;
    private LocalDate date;
    private double amount;
    private String category;
    private String note;

    public Expense(int id, LocalDate date, double amount, String category, String note) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.note = note == null ? "" : note;
    }

    public int getId() { return id; }
    public LocalDate getDate() { return date; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getNote() { return note; }

    public void setDate(LocalDate date) { this.date = date; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
    public void setNote(String note) { this.note = note; }

    public String toCsvLine() {
        String safeNote = note.replace(",", ";");
        return id + "," + date + "," + amount + "," + category + "," + safeNote;
    }

    public static Expense fromCsvLine(String line) {
        String[] parts = line.split(",", 5);
        int id = Integer.parseInt(parts[0]);
        LocalDate date = LocalDate.parse(parts[1]);
        double amount = Double.parseDouble(parts[2]);
        String category = parts[3];
        String note = parts.length > 4 ? parts[4] : "";
        return new Expense(id, date, amount, category, note);
    }

    @Override
    public String toString() {
        return String.format("[%d] %s | %-12s | $%-10.2f | %s", id, date, category, amount, note);
    }
}