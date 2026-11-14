package myweightfitness;


import java.util.ArrayList;

/**
 * CATEGORY: DATA
 * Stores and displays exercise information.
 * Proposal methods: viewExercise(), searchExercise(), displayInstructions().
 */
public class ExerciseLibrary {
    private ArrayList<Exercise> exercises = new ArrayList<>();

    public ExerciseLibrary() {
        loadExercises();
    }

    private void loadExercises() {
        exercises.add(new Exercise("Push Up", "Chest/Triceps", "Keep body straight, lower chest to floor", "Bodyweight"));
        exercises.add(new Exercise("Squat", "Legs", "Feet shoulder-width, lower hips back", "Bodyweight/Dumbbell"));
        exercises.add(new Exercise("Plank", "Core", "Hold straight line 30-60s", "Bodyweight"));
        exercises.add(new Exercise("Dumbbell Curl", "Biceps", "Curl weight toward shoulder", "Dumbbell"));
        exercises.add(new Exercise("Resistance Band Pull", "Back", "Pull band toward chest", "Resistance Band"));
        exercises.add(new Exercise("Bench Press", "Chest", "Lower bar to chest, press up", "Bench, Barbell"));
        exercises.add(new Exercise("Lunge", "Legs", "Step forward, lower knees to 90°", "Bodyweight"));
        exercises.add(new Exercise("Shoulder Press", "Shoulders", "Press weights overhead", "Dumbbell"));
    }

    /** View every exercise in the library. */
    public ArrayList<Exercise> viewExercise() {
        return new ArrayList<>(exercises);
    }

    /** View one exercise by its name (null if not found). */
    public Exercise viewExercise(String exerciseName) {
        for (Exercise ex : exercises) {
            if (ex.getExerciseName().equalsIgnoreCase(exerciseName.trim())) {
                return ex;
            }
        }
        return null;
    }

    /** Search by exercise name or target muscle. */
    public ArrayList<Exercise> searchExercise(String keyword) {
        ArrayList<Exercise> result = new ArrayList<>();
        String key = keyword.trim().toLowerCase();
        for (Exercise ex : exercises) {
            if (ex.getExerciseName().toLowerCase().contains(key)
                    || ex.getTargetMuscle().toLowerCase().contains(key)) {
                result.add(ex);
            }
        }
        return result;
    }

    /** Instructions for one exercise. */
    public String displayInstructions(String exerciseName) {
        Exercise ex = viewExercise(exerciseName);
        if (ex == null) return "Exercise not found.";
        return ex.getExerciseName() + " (" + ex.getTargetMuscle() + ")\n"
                + ex.getInstructions() + "\nEquipment: " + ex.getEquipment();
    }

    /** Only the exercises the user can do with the equipment they have. */
    public ArrayList<Exercise> getAvailableExercises(ArrayList<String> availableEquipment) {
        ArrayList<Exercise> result = new ArrayList<>();
        for (Exercise ex : exercises) {
            if (ex.canBePerformedWith(availableEquipment)) {
                result.add(ex);
            }
        }
        return result;
    }
}
