
package myweightfitness;

/**
 * CATEGORY: LOGIC
 * Monitors workout improvements and suggests gradual load increases.
 * Methods: trackProgressiveLoad(), recommendNextLoad().
 */
public class ProgressiveLoadTracker {

    private static final int TARGET_REPS = 12;
    private static final double WEIGHT_INCREMENT = 2.5;

    private double currentWeight;
    private double previousWeight;
    private int currentReps;
    private int previousReps;
    private boolean hasRecord = false;

    // Saves the workout details and checks the changes from the previous session.
    public String trackProgressiveLoad(double weight, int reps) {

        if (weight < 0 || reps <= 0) {
            throw new IllegalArgumentException(
                    "Invalid input. Weight must not be negative and reps must be at least 1."
            );
        }

        previousWeight = currentWeight;
        previousReps = currentReps;

        currentWeight = weight;
        currentReps = reps;

        if (!hasRecord) {
            hasRecord = true;
            return "Your first workout session has been recorded!";
        }

        return String.format(
                "Progress from last session: %+.1f kg, %+d reps",
                currentWeight - previousWeight,
                currentReps - previousReps
        );
    }

    // Provides a recommendation for the next workout session.
    public String recommendNextLoad() {

        if (!hasRecord) {
            return "No workout records available yet.";
        }

        if (currentWeight == 0) {
            return "Next session: Try to complete "
                    + (currentReps + 2) + " reps.";
        }

        if (currentReps >= TARGET_REPS) {
            return "Next session: Increase your weight to "
                    + (currentWeight + WEIGHT_INCREMENT)
                    + " kg and begin with "
                    + (TARGET_REPS - 4) + " reps.";
        }

        return "Next session: Maintain "
                + currentWeight + " kg and aim for "
                + (currentReps + 1) + " reps.";
    }

    // Getter methods
    public double getCurrentWeight() {
        return currentWeight;
    }

    public double getPreviousWeight() {
        return previousWeight;
    }

    public int getCurrentReps() {
        return currentReps;
    }

    public int getPreviousReps() {
        return previousReps;
    }
}
