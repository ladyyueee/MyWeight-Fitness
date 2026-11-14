package myweightfitness;

/**
 * CATEGORY: LOGIC
 * Tracks workout progression and recommends gradual increases (progressive overload).
 * Proposal methods: trackProgressiveLoad(), recommendNextLoad().
 */
public class ProgressiveLoadTracker {
    private static final int TARGET_REPS = 12;        // at this many reps, add weight
    private static final double WEIGHT_STEP = 2.5;    // kg added each time

    private double currentWeight;
    private double previousWeight;
    private int currentReps;
    private int previousReps;
    private boolean hasRecord = false;

    /** Records a session and returns how it compares with the last one. */
    public String trackProgressiveLoad(double weight, int reps) {
        if (weight < 0 || reps <= 0) {
            throw new IllegalArgumentException("Weight cannot be negative and reps must be at least 1.");
        }
        previousWeight = currentWeight;
        previousReps = currentReps;
        currentWeight = weight;
        currentReps = reps;

        if (!hasRecord) {
            hasRecord = true;
            return "First session recorded.";
        }
        return String.format("Change since last session: %+.1f kg, %+d reps",
                currentWeight - previousWeight, currentReps - previousReps);
    }

    /** Suggests what to do next session. */
    public String recommendNextLoad() {
        if (!hasRecord) {
            return "No workout recorded yet.";
        }
        if (currentWeight == 0) { // bodyweight exercise
            return "Next session: aim for " + (currentReps + 2) + " reps.";
        }
        if (currentReps >= TARGET_REPS) {
            return "Next session: increase to " + (currentWeight + WEIGHT_STEP)
                    + " kg and start again at " + (TARGET_REPS - 4) + " reps.";
        }
        return "Next session: keep " + currentWeight + " kg and aim for " + (currentReps + 1) + " reps.";
    }

    public double getCurrentWeight() { return currentWeight; }
    public double getPreviousWeight() { return previousWeight; }
    public int getCurrentReps() { return currentReps; }
    public int getPreviousReps() { return previousReps; }
}
