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
        // FIX 1: Prevent recording more workouts than scheduled for the cycle
        if (getRecordCountInCurrentCycle() >= scheduledWorkouts) {
            System.out.println("⚠️ Cannot record: Scheduled workouts for this cycle are already full.");
            return;
        }
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

    /** Summary of the current cycle, e.g. "8 completed, 4 missed (67%)". */
    public String completionStatus() {
        int completed = 0;
        int missed = 0;
        
        // Loop through only the current cycle
        for (int i = cycleStart; i < records.size(); i++) {
            if (records.get(i).getStatus().equals(WorkoutRecord.COMPLETED)) {
                completed++;
            } else {
                missed++;
            }
        }
        
        // FIX 2: Calculate unlogged workouts as "missed" to accurately reflect the schedule
        int unlogged = scheduledWorkouts - (completed + missed);
        if (unlogged > 0) {
            missed += unlogged;
        }

        return completed + " completed, " + missed + " missed ("
                + String.format("%.0f", getCompletionRate()) + "%)";
    }

    /** Completed workouts in the current cycle, as a percentage of the scheduled ones. */
    public double getCompletionRate() {
        if (scheduledWorkouts <= 0) return 0;
        
        int completed = 0;
        for (int i = cycleStart; i < records.size(); i++) {
            if (records.get(i).getStatus().equals(WorkoutRecord.COMPLETED)) {
                completed++;
            }
        }
        
        // FIX 3: Ensure rate never exceeds 100% even if extra workouts are logged
        double rate = (completed * 100.0) / scheduledWorkouts;
        return Math.min(100.0, rate);
    }

    /** Called after a promotion so the completion rate starts again from 0%. */
    public void startNewCycle() {
        cycleStart = records.size();
    }

    // ================= HELPER METHODS (For GUI / Logic) =================

    /** Counts how many logs exist in the current cycle */
    private int getRecordCountInCurrentCycle() {
        return records.size() - cycleStart;
    }

    public int getScheduledWorkouts() { return scheduledWorkouts; }
    
    public void setScheduledWorkouts(int scheduledWorkouts) { 
        if (scheduledWorkouts > 0) {
            this.scheduledWorkouts = scheduledWorkouts; 
        }
    }
}
