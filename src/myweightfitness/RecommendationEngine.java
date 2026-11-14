package myweightfitness;

import java.util.ArrayList;

/**
 * CATEGORY: LOGIC
 * Generates workout plans and calorie / water targets.
 * Proposal methods: generateWorkoutPlan(), calculateCalorieTarget(), assignMuscleRotation().
 */
public class RecommendationEngine {
    // Proposal attributes (filled in from the user being processed)
    private String fitnessLevel;
    private String category;
    private double goalWeight;
    private double dailyCalories;

    private ExerciseLibrary library;

    public RecommendationEngine(ExerciseLibrary library) {
        this.library = library;
    }

    /** Copies the data this engine needs from the user. */
    private void readProfile(User user) {
        this.fitnessLevel = user.getFitnessLevel();
        this.category = user.getCategory();
        this.goalWeight = user.getTargetWeight();
    }

    // ================= Daily calorie target =================
    public double calculateCalorieTarget(User user) {
        readProfile(user);

        // Mifflin-St Jeor equation (the last constant depends on sex)
        double bmr = (10 * user.getCurrentWeight()) + (6.25 * user.getHeight()) - (5 * user.getAge());
        String gender = user.getGender();
        if (gender.equalsIgnoreCase("Male")) {
            bmr += 5;
        } else if (gender.equalsIgnoreCase("Female")) {
            bmr -= 161;
        } else {
            bmr -= 78; // sex not given: use the middle of +5 and -161
        }

        double multiplier = 1.2; // Beginner
        if (fitnessLevel.equals("Intermediate")) multiplier = 1.55;
        if (fitnessLevel.equals("Advanced")) multiplier = 1.725;

        double tdee = bmr * multiplier;

        // Adjust based on weight goal
        if (goalWeight < user.getCurrentWeight()) {
            tdee -= 500; // deficit for weight loss
        } else if (goalWeight > user.getCurrentWeight()) {
            tdee += 300; // surplus for weight gain
        }

        tdee = Math.max(1200, tdee); // safety minimum
        dailyCalories = Math.round(tdee);
        return dailyCalories;
    }

    // ================= Workout plan (Polymorphism) =================
    public String generateWorkoutPlan(User user) {
        readProfile(user);
        if (user instanceof GymUser) {
            assignMuscleRotation((GymUser) user);
        }
        // Calls the version in GymUser, Athlete or RehabPatient
        return user.generateWorkoutPlan();
    }

    /** Beginner gym users get a balanced monthly muscle-group rotation. */
    public void assignMuscleRotation(GymUser gymUser) {
        if (gymUser.getFitnessLevel().equals("Beginner")) {
            gymUser.setMonthlyRotation(new String[]{
                "Chest & Triceps",
                "Back & Biceps",
                "Legs & Shoulders",
                "Core & Cardio"
            });
        } else {
            gymUser.setMonthlyRotation(new String[0]); // they choose their own target muscle
        }
    }

    // ================= Exercises based on the user's equipment =================
    public ArrayList<Workout> recommendWorkouts(User user) {
        readProfile(user);
        ArrayList<Workout> workouts = new ArrayList<>();
        if (!user.isReadyToTrain()) {
            return workouts; // e.g. a rehab patient with high pain level: rest
        }
        for (Exercise ex : library.getAvailableExercises(Equipment.getAvailableEquipmentNames())) {
            Workout workout = new Workout(ex.getExerciseName(), ex.getEquipment());
            workout.generateWorkout(fitnessLevel);
            workouts.add(workout);
        }
        return workouts;
    }

    // ================= Monthly schedule (text shown on the dashboard) =================
    public String generateMonthlySchedule(User user, double calories) {
        StringBuilder sb = new StringBuilder();
        sb.append("📅 MONTHLY FITNESS SCHEDULE\n");
        sb.append("═══════════════════════════════\n");
        sb.append("Target Calories/Day: ").append((int) calories).append(" kcal\n");
        sb.append("BMI: ").append(String.format("%.1f", user.calculateBMI()))
          .append(" (").append(user.getBMIStatus()).append(")\n\n");
        sb.append(generateWorkoutPlan(user));

        ArrayList<Workout> workouts = recommendWorkouts(user);
        if (!workouts.isEmpty()) {
            sb.append("\nRECOMMENDED EXERCISES\n");
            for (Workout workout : workouts) {
                sb.append("- ").append(workout.displayWorkout()).append("\n");
            }
        }

        sb.append("\n💧 Hydration Goal: ").append(calculateWaterGoal(user)).append(" L/day\n");
        return sb.toString();
    }

    // ================= Water goal =================
    public double calculateWaterGoal(User user) {
        // 35 ml per kg of body weight
        return Math.round((user.getCurrentWeight() * 0.035) * 10) / 10.0;
    }

    // Getters for the proposal attributes
    public String getFitnessLevel() { return fitnessLevel; }
    public String getCategory() { return category; }
    public double getGoalWeight() { return goalWeight; }
    public double getDailyCalories() { return dailyCalories; }
}