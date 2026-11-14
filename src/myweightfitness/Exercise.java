package myweightfitness;

import java.util.ArrayList;

/**
 * CATEGORY: DATA
 * One exercise record: exerciseName, targetMuscle, instructions, equipment.
 * (These are the ExerciseLibrary attributes from the proposal.)
 *
 * Equipment format: "," means ALL are needed, "/" means ANY ONE is enough.
 *   "Bench, Barbell"      -> needs a bench AND a barbell
 *   "Bodyweight/Dumbbell" -> no equipment needed, or a dumbbell
 */
public class Exercise {
    private String exerciseName;
    private String targetMuscle;
    private String instructions;
    private String equipment;

    public Exercise(String exerciseName, String targetMuscle, String instructions, String equipment) {
        this.exerciseName = exerciseName;
        this.targetMuscle = targetMuscle;
        this.instructions = instructions;
        // FIX 1: Prevent NullPointerException if equipment is null
        this.equipment = (equipment == null || equipment.trim().isEmpty()) ? "Bodyweight" : equipment;
    }

    public String getExerciseName() { return exerciseName; }
    public String getTargetMuscle() { return targetMuscle; }
    public String getInstructions() { return instructions; }
    public String getEquipment() { return equipment; }

    /** 
     * True if the user has what this exercise needs. 
     */
    public boolean canBePerformedWith(ArrayList<String> availableEquipment) {
        // FIX 2: If user equipment list is null, only bodyweight exercises can be done
        if (availableEquipment == null) {
            availableEquipment = new ArrayList<>();
        }

        for (String requirement : equipment.split(",")) {      // every requirement must be met
            boolean met = false;
            for (String option : requirement.split("/")) {      // any option is enough
                String name = option.trim();
                if (name.equalsIgnoreCase("Bodyweight") || hasEquipment(availableEquipment, name)) {
                    met = true;
                    break; // FIX 3: Added break to stop checking options once one is met
                }
            }
            if (!met) return false;
        }
        return true;
    }

    private boolean hasEquipment(ArrayList<String> list, String name) {
        for (String item : list) {
            if (item.equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    // ================= FIX 4: Added Methods from the Proposal =================

    /**
     * Displays the step-by-step instructions and required equipment.
     * (Matches the 'displayInstructions()' method in the proposal)
     */
    public String displayInstructions() {
        return "🏋️ Exercise: " + exerciseName + "\n" +
               "🎯 Target Muscle: " + targetMuscle + "\n" +
               "🛠️ Equipment Needed: " + equipment + "\n" +
               "📋 How to do it: " + instructions;
    }

    /**
     * Helper method for the Exercise Library's search feature.
     * (Matches the 'searchExercise()' requirement)
     */
    public boolean matchesKeyword(String keyword) {
        if (keyword == null) return false;
        String lowerKeyword = keyword.toLowerCase();
        return exerciseName.toLowerCase().contains(lowerKeyword) ||
               targetMuscle.toLowerCase().contains(lowerKeyword) ||
               equipment.toLowerCase().contains(lowerKeyword);
    }

    /**
     * Clean string representation for printing in the GUI or Console.
     */
    @Override
    public String toString() {
        return exerciseName + " [" + targetMuscle + "]";
    }
}