
package myweightfitness;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * CATEGORY: DATA
 * Stores an individual workout record including its date, name, and status.
 */
public class WorkoutRecord {

    public static final String COMPLETED = "Completed";
    public static final String MISSED = "Missed";

    private LocalDate workoutDate;
    private String workoutName;
    private String status;

    // Creates a new workout record.
    public WorkoutRecord(String workoutName, String status) {
        this.workoutDate = LocalDate.now();
        this.workoutName = workoutName;
        this.status = status;
    }

    // Getters
    public LocalDate getWorkoutDate() {
        return workoutDate;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public String getStatus() {
        return status;
    }

    // Updates the workout name.
    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    // Changes the workout status.
    public void setStatus(String status) {
        if (status.equals(COMPLETED) || status.equals(MISSED)) {
            this.status = status;
        }
    }

    // Checks if the workout is completed.
    public boolean isCompleted() {
        return status.equals(COMPLETED);
    }

    // Checks if the workout was missed.
    public boolean isMissed() {
        return status.equals(MISSED);
    }

    // Returns the date in a readable format.
    public String getFormattedDate() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MMMM dd, yyyy");

        return workoutDate.format(formatter);
    }

    // Displays the workout record.
    @Override
    public String toString() {
        String mark = isCompleted() ? "[✓] " : "[✗] ";

        return mark + getFormattedDate() + " | "
                + workoutName + " - " + status.toUpperCase();
    }
}
