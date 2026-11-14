package myweightfitness;

/**
 * CATEGORY: LOGIC
 * Manages fitness-level advancement.
 * Proposal methods: checkFitnessLevel(), promoteLevel().
 */
public class FitnessLevelManager {
    private static final double PROMOTION_RATE = 80; // % of workouts that must be completed

    private String currentLevel;
    private double completionRate;

    /** Checks the completion rate and promotes the user when the requirement is met. */
    public String checkFitnessLevel(User user, WorkoutHistory history) {
        currentLevel = user.getFitnessLevel();
        completionRate = history.getCompletionRate();

        if (completionRate < PROMOTION_RATE) {
            return "Keep going! Completion rate: " + String.format("%.0f", completionRate) + "%";
        }
        if (promoteLevel(user)) {
            history.startNewCycle(); // the rate starts again from 0% at the new level
            currentLevel = user.getFitnessLevel();
            return "🎉 Promoted to " + currentLevel.toUpperCase() + "!";
        }
        return "Already at Advanced level.";
    }

    /** Moves the user up one level. Returns false if already Advanced. */
    public boolean promoteLevel(User user) {
        String level = user.getFitnessLevel();
        if (level.equals("Beginner")) {
            user.setFitnessLevel("Intermediate");
            return true;
        }
        if (level.equals("Intermediate")) {
            user.setFitnessLevel("Advanced");
            return true;
        }
        return false;
    }

    public String getCurrentLevel() { return currentLevel; }
    public double getCompletionRate() { return completionRate; }
}
