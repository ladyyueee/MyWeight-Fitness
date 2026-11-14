package myweightfitness;

import java.util.ArrayList;

/**
 * CATEGORY: DATA
 * Keeps the log of completed and missed workouts.
 * Proposal methods: recordWorkoutHistory(), viewHistory(), completionStatus().
 */
public class WorkoutHistory {
    private ArrayList<WorkoutRecord> records = new ArrayList<>();
    private int scheduledWorkouts = 12; // workouts planned per cycle (one month)
    private int cycleStart = 0;         // index where the current cycle begins

    /** Record a workout as WorkoutRecord.COMPLETED or WorkoutRecord.MISSED. */
    public void recordWorkoutHistory(String workoutName, String status) {
        records.add(new WorkoutRecord(workoutName, status));
    }

    /** The whole history as text lines (oldest first). */
    public ArrayList<String> viewHistory() {
        ArrayList<String> lines = new ArrayList<>();
        for (WorkoutRecord record : records) {
            lines.add(record.toString());
        }
        return lines;
    }

    /** Summary of the current cycle, e.g. "8 completed, 2 missed (67%)". */
    public String completionStatus() {
        int completed = 0;
        int missed = 0;
        for (int i = cycleStart; i < records.size(); i++) {
            if (records.get(i).getStatus().equals(WorkoutRecord.COMPLETED)) completed++;
            else missed++;
        }
        return completed + " completed, " + missed + " missed ("
                + String.format("%.0f", getCompletionRate()) + "%)";
    }

    /** Completed workouts in the current cycle, as a percentage of the scheduled ones. */
    public double getCompletionRate() {
        if (scheduledWorkouts <= 0) return 0;
        int completed = 0;
        for (int i = cycleStart; i < records.size(); i++) {
            if (records.get(i).getStatus().equals(WorkoutRecord.COMPLETED)) completed++;
        }
        return Math.min(100.0, (completed * 100.0) / scheduledWorkouts);
    }

    /** Called after a promotion so the completion rate starts again from 0%. */
    public void startNewCycle() {
        cycleStart = records.size();
    }

    public int getScheduledWorkouts() { return scheduledWorkouts; }
    public void setScheduledWorkouts(int scheduledWorkouts) { this.scheduledWorkouts = scheduledWorkouts; }
}
