
package myweightfitness;

import java.util.ArrayList;

/**
 * CATEGORY: LOGIC
 * Handles the workout schedule, calorie goal, water intake, and weight tracking.
 * Methods: showFitnessPlan(), updatePlan(), trackWater(), updateProgress().
 */
public class FitnessPlan {

    private ArrayList<Workout> workoutSchedule = new ArrayList<>();
    private double dailyCalories;
    private double waterIntake;
    private ArrayList<Double> waterLog = new ArrayList<>();

    private double startWeight;
    private double currentWeight;
    private double targetWeight;
    private double progressRate;

    public FitnessPlan(User user, double dailyCalories) {
        this.dailyCalories = dailyCalories;
        this.startWeight = user.getCurrentWeight();
        this.currentWeight = user.getCurrentWeight();
        this.targetWeight = user.getTargetWeight();

        updateProgress(currentWeight);
    }

    // Adds a workout to the schedule.
    public void addWorkout(Workout workout) {
        workoutSchedule.add(workout);
    }

    // Updates the workout schedule and daily calorie goal.
    public void updatePlan(ArrayList<Workout> newSchedule, double newDailyCalories) {
        this.workoutSchedule = new ArrayList<>(newSchedule);
        this.dailyCalories = newDailyCalories;
    }

    // Records the amount of water consumed.
    public double trackWater(double liters) {

        if (liters <= 0) {
            throw new IllegalArgumentException(
                    "Water intake should be greater than 0."
            );
        }

        waterLog.add(liters);
        waterIntake += liters;

        return waterIntake;
    }

    // Calculates the user's progress toward the target weight.
    public void updateProgress(double newWeight) {

        this.currentWeight = newWeight;

        double weightDifference = targetWeight - startWeight;

        if (weightDifference == 0) {
            progressRate = 100;
        } else {
            progressRate = ((currentWeight - startWeight)
                    / weightDifference) * 100;
        }

        progressRate = Math.max(0, Math.min(100, progressRate));
    }

    // Displays the complete fitness plan.
    public String showFitnessPlan() {

        StringBuilder sb = new StringBuilder();

        sb.append("MY FITNESS PLAN\n");
        sb.append("Daily Calories: ")
                .append((int) dailyCalories).append(" kcal\n");

        sb.append("Water Intake: ")
                .append(String.format("%.1f", waterIntake)).append(" L\n");

        sb.append("Current Weight: ")
                .append(currentWeight).append(" kg -> ")
                .append(targetWeight).append(" kg (")
                .append(String.format("%.0f", progressRate))
                .append("% completed)\n");

        sb.append("Workout Schedule:\n");

        if (workoutSchedule.isEmpty()) {
            sb.append("- No workouts scheduled yet.\n");
        }

        for (Workout workout : workoutSchedule) {
            sb.append("- ")
                    .append(workout.displayWorkout())
                    .append("\n");
        }

        return sb.toString();
    }

    // Getter methods
    public ArrayList<Workout> getWorkoutSchedule() {
        return new ArrayList<>(workoutSchedule);
    }

    public double getDailyCalories() {
        return dailyCalories;
    }

    public double getWaterIntake() {
        return waterIntake;
    }

    public ArrayList<Double> getWaterLog() {
        return new ArrayList<>(waterLog);
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    public double getProgressRate() {
        return progressRate;
    }
}
