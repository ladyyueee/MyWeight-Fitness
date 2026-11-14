package myweightfitness;

import java.time.LocalDate;

/**
 * CATEGORY: DATA
 * One line in the workout history: workoutDate, workoutName, status.
 */
public class WorkoutRecord {
    public static final String COMPLETED = "Completed";
    public static final String MISSED = "Missed";

    private LocalDate workoutDate;
    private String workoutName;
    private String status;

    public WorkoutRecord(String workoutName, String status) {
        this.workoutDate = LocalDate.now();
        this.workoutName = workoutName;
        this.status = status;
    }

    public LocalDate getWorkoutDate() { return workoutDate; }
    public String getWorkoutName() { return workoutName; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        String mark = status.equals(COMPLETED) ? "[✓] " : "[✗] ";
        return mark + workoutDate + " | " + workoutName + " - " + status.toUpperCase();
    }
}
