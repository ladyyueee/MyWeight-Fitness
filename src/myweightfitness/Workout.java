
package myweightfitness;

/**
 * CATEGORY: MODEL
 * Represents a workout with its exercise name, sets, reps, duration,
 * equipment, and suggested weight.
 * Methods: generateWorkout(), setDuration(), displayWorkout().
 */
public class Workout {

    private String workoutName;
    private int duration;           // minutes
    private int sets;
    private int repetitions;
    private String equipment;
    private double suggestedWeight;

    public Workout(String workoutName, String equipment) {
        this.workoutName = workoutName;
        this.equipment = equipment;
        this.duration = 15;
        this.sets = 3;
        this.repetitions = 10;
        this.suggestedWeight = 0;
    }

    // Generates workout details based on the user's fitness level.
    public void generateWorkout(String fitnessLevel) {

        int level = 0;

        if ("Intermediate".equals(fitnessLevel)) {
            level = 1;
        } else if ("Advanced".equals(fitnessLevel)) {
            level = 2;
        }

        int[] setsByLevel = {3, 4, 5};
        int[] repsByLevel = {10, 12, 15};
        int[] durationByLevel = {15, 20, 30};

        this.sets = setsByLevel[level];
        this.repetitions = repsByLevel[level];
        this.duration = durationByLevel[level];

        // Assigns a suggested weight depending on the equipment.
        String eq = (equipment == null) ? "" : equipment.toLowerCase();

        if (eq.contains("bodyweight")) {
            this.suggestedWeight = 0;

        } else if (eq.contains("barbell")) {
            double[] weights = {20, 30, 40};
            this.suggestedWeight = weights[level];

        } else if (eq.contains("dumbbell")) {
            double[] weights = {5, 8, 12};
            this.suggestedWeight = weights[level];

        } else {
            this.suggestedWeight = 0;
        }
    }

    // Changes the workout duration.
    public void setDuration(int minutes) {
        if (minutes <= 0) {
            throw new IllegalArgumentException(
                    "Workout duration must be greater than 0."
            );
        }

        this.duration = minutes;
    }

    // Updates the equipment used for the exercise.
    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    // Calculates the total number of repetitions in a workout.
    public int calculateTotalReps() {
        return sets * repetitions;
    }

    // Returns the total number of sets.
    public int calculateTotalSets() {
        return sets;
    }

    // Returns the total workout time.
    public int getTotalWorkoutTime() {
        return duration;
    }

    // Provides a short summary of the workout.
    public String getWorkoutSummary() {
        return workoutName + ": " + sets + " sets, "
                + repetitions + " reps, " + duration + " minutes.";
    }

    // Displays the complete workout information.
    public String displayWorkout() {

        String weight = (suggestedWeight > 0)
                ? ", " + suggestedWeight + " kg"
                : ", bodyweight";

        return workoutName + " - "
                + sets + " sets x "
                + repetitions + " reps, "
                + duration + " min"
                + weight + " (" + equipment + ")";
    }

    // ================= Getters and Setters =================

    public String getWorkoutName() {
        return workoutName;
    }

    public int getDuration() {
        return duration;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        if (sets <= 0) {
            throw new IllegalArgumentException("Sets must be greater than 0.");
        }
        this.sets = sets;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        if (repetitions <= 0) {
            throw new IllegalArgumentException("Repetitions must be greater than 0.");
        }
        this.repetitions = repetitions;
    }

    public String getEquipment() {
        return equipment;
    }

    public double getSuggestedWeight() {
        return suggestedWeight;
    }

    public void setSuggestedWeight(double weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative.");
        }
        this.suggestedWeight = weight;
    }
}
