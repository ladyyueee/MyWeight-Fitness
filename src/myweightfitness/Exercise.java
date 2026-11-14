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
        this.equipment = equipment;
    }

    public String getExerciseName() { return exerciseName; }
    public String getTargetMuscle() { return targetMuscle; }
    public String getInstructions() { return instructions; }
    public String getEquipment() { return equipment; }

    /** True if the user has what this exercise needs. */
    public boolean canBePerformedWith(ArrayList<String> availableEquipment) {
        for (String requirement : equipment.split(",")) {      // every requirement must be met
            boolean met = false;
            for (String option : requirement.split("/")) {      // any option is enough
                String name = option.trim();
                if (name.equalsIgnoreCase("Bodyweight") || hasEquipment(availableEquipment, name)) {
                    met = true;
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
}
