package myweightfitness;

/**
 * CATEGORY: MODEL
 * Gym Enthusiast. Inherits everything from User (Inheritance)
 * and overrides generateWorkoutPlan() (Polymorphism).
 */
public class GymUser extends User {
    private String targetMuscleGroup;
    private String[] monthlyRotation; // assigned by RecommendationEngine.assignMuscleRotation()

    public GymUser() {
        super();
        setCategory("Gym Enthusiast");
        this.targetMuscleGroup = "Full Body";
        this.monthlyRotation = new String[0];
    }

    public String getTargetMuscleGroup() { return targetMuscleGroup; }
    public void setTargetMuscleGroup(String group) { this.targetMuscleGroup = group; }

    public String[] getMonthlyRotation() { return monthlyRotation; }
    public void setMonthlyRotation(String[] rotation) {
        this.monthlyRotation = (rotation == null) ? new String[0] : rotation;
    }

    @Override
    public String generateWorkoutPlan() {
        String plan;
        if (getFitnessLevel().equals("Beginner")) {
            plan = "MONTHLY ROTATION (Beginner)\n";
            if (monthlyRotation.length == 0) {
                plan += "Rotation not assigned yet.\n";
            }
            for (int i = 0; i < monthlyRotation.length; i++) {
                plan += "Week " + (i + 1) + ": " + monthlyRotation[i] + "\n";
            }
        } else {
            plan = "TARGETED PLAN (" + getFitnessLevel() + ")\n";
            plan += "Focus: " + targetMuscleGroup + "\n";
            plan += "3x/week strength training\n";
        }
        return plan;
    }
}
