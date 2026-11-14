package myweightfitness;

import java.util.ArrayList;

/**
 * CATEGORY: LOGIC
 * Combines the workout schedule, calorie target, water intake and weight progress.
 * Proposal methods: showFitnessPlan(), updatePlan(), trackWater(), updateProgress().
 */
public class FitnessPlan {
    private ArrayList<Workout> workoutSchedule = new ArrayList<>();
    private double dailyCalories;
    private double waterIntake;                      // liters logged today
    private ArrayList<Double> waterLog = new ArrayList<>();
    private double startWeight;
    private double currentWeight;
    private double targetWeight;
    private double progressRate;                     // % of the way to the target weight

    public FitnessPlan(User user, double dailyCalories) {
        this.dailyCalories = dailyCalories;
        this.startWeight = user.getCurrentWeight();
        this.currentWeight = user.getCurrentWeight();
        this.targetWeight = user.getTargetWeight();
        updateProgress(this.currentWeight);
    }

    public void addWorkout(Workout workout) {
        workoutSchedule.add(workout);
    }

    /** Replaces the schedule and calorie target (for example after a level promotion). */
    public void updatePlan(ArrayList<Workout> newSchedule, double newDailyCalories) {
        this.workoutSchedule = new ArrayList<>(newSchedule);
        this.dailyCalories = newDailyCalories;
    }

    /** Logs water (in liters) and returns the total for today. */
    public double trackWater(double liters) {
        if (liters <= 0) {
            throw new IllegalArgumentException("Water amount must be greater than 0.");
        }
        waterLog.add(liters);
        waterIntake += liters;
        return waterIntake;
    }

    /** Updates the current weight and the progress toward the target weight. */
    public void updateProgress(double newWeight) {
        this.currentWeight = newWeight;
        double totalChange = targetWeight - startWeight;
        if (totalChange == 0) {
            progressRate = 100;
        } else {
            progressRate = ((currentWeight - startWeight) / totalChange) * 100;
        }
        progressRate = Math.max(0, Math.min(100, progressRate));
    }

    public String showFitnessPlan() {
        StringBuilder sb = new StringBuilder();
        sb.append("FITNESS PLAN\n");
        sb.append("Daily calories: ").append((int) dailyCalories).append(" kcal\n");
        sb.append("Water today: ").append(String.format("%.1f", waterIntake)).append(" L\n");
        sb.append("Weight: ").append(currentWeight).append(" kg -> ").append(targetWeight)
          .append(" kg (").append(String.format("%.0f", progressRate)).append("% done)\n");
        sb.append("Workouts:\n");
        if (workoutSchedule.isEmpty()) {
            sb.append("- none scheduled\n");
        }
        for (Workout workout : workoutSchedule) {
            sb.append("- ").append(workout.displayWorkout()).append("\n");
        }
        return sb.toString();
    }

    public ArrayList<Workout> getWorkoutSchedule() { return new ArrayList<>(workoutSchedule); }
    public double getDailyCalories() { return dailyCalories; }
    public double getWaterIntake() { return waterIntake; }
    public ArrayList<Double> getWaterLog() { return new ArrayList<>(waterLog); }
    public double getCurrentWeight() { return currentWeight; }
    public double getProgressRate() { return progressRate; }
}
