package myweightfitness;

/**
 * CATEGORY: MODEL
 * One workout (exercise + sets, reps, duration, suggested weight).
 * Proposal methods: generateWorkout(), setDuration(), displayWorkout().
 */
public class Workout {
    private String workoutName;
    private int duration;            // in minutes
    private int sets;
    private int repetitions;
    private String equipment;
    private double suggestedWeight;  // in kg (0 = bodyweight)

    public Workout(String workoutName, String equipment) {
        this.workoutName = workoutName;
        this.equipment = equipment;
        this.duration = 15;
        this.sets = 3;
        this.repetitions = 10;
        this.suggestedWeight = 0;
    }

    /** Sets the sets, reps, duration and suggested weight based on the fitness level. */
    public void generateWorkout(String fitnessLevel) {
        int level = 0; // Beginner
        if ("Intermediate".equals(fitnessLevel)) level = 1;
        else if ("Advanced".equals(fitnessLevel)) level = 2;

        int[] setsByLevel = {3, 4, 5};
        int[] repsByLevel = {10, 12, 15};
        int[] minutesByLevel = {15, 20, 30};
        this.sets = setsByLevel[level];
        this.repetitions = repsByLevel[level];
        this.duration = minutesByLevel[level];

        // Suggested weight only for weighted equipment
        String eq = (equipment == null) ? "" : equipment.toLowerCase();
        if (eq.contains("bodyweight")) {
            this.suggestedWeight = 0; // can be done without weights
        } else if (eq.contains("barbell")) {
            double[] barbell = {20, 30, 40};
            this.suggestedWeight = barbell[level];
        } else if (eq.contains("dumbbell")) {
            double[] dumbbell = {5, 8, 12};
            this.suggestedWeight = dumbbell[level];
        } else {
            this.suggestedWeight = 0;
        }
    }

    /** Lets the user customize the duration (in minutes). */
    public void setDuration(int minutes) {
        if (minutes <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0.");
        }
        this.duration = minutes;
    }

    public String displayWorkout() {
        String weight = (suggestedWeight > 0) ? ", " + suggestedWeight + " kg" : ", bodyweight";
        return workoutName + " - " + sets + " sets x " + repetitions + " reps, "
                + duration + " min" + weight + " (" + equipment + ")";
    }

    // Getters and setters
    public String getWorkoutName() { return workoutName; }
    public int getDuration() { return duration; }
    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }
    public int getRepetitions() { return repetitions; }
    public void setRepetitions(int repetitions) { this.repetitions = repetitions; }
    public String getEquipment() { return equipment; }
    public double getSuggestedWeight() { return suggestedWeight; }
    public void setSuggestedWeight(double weight) { this.suggestedWeight = weight; }
}
