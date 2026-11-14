package myweightfitness;

/**
 * CATEGORY: MODEL
 * Rehabilitation Patient. Inherits from User, keeps a private pain level
 * (Safety Feedback Loop) and overrides generateWorkoutPlan().
 */
public class RehabPatient extends User {
    private int painLevel; // 0 (no pain) to 10 (worst pain)
    private String recoveryFocus;

    public RehabPatient() {
        super();
        setCategory("Rehabilitation Patient");
        this.painLevel = 0;
        this.recoveryFocus = "General Recovery";
    }

    public int getPainLevel() { return painLevel; }

    public void setPainLevel(int level) {
        if (level < 0 || level > 10) {
            throw new IllegalArgumentException("Pain level must be from 0 to 10.");
        }
        this.painLevel = level;
    }

    public String getRecoveryFocus() { return recoveryFocus; }
    public void setRecoveryFocus(String focus) { this.recoveryFocus = focus; }

    /** Pain of 3 or less is considered safe for light exercise. */
    @Override
    public boolean isReadyToTrain() {
        return painLevel <= 3;
    }

    @Override
    public String generateWorkoutPlan() {
        String plan = "RECOVERY PLAN (" + recoveryFocus + ")\n";
        if (isReadyToTrain()) {
            plan += "- Gentle stretching (daily)\n";
            plan += "- Light mobility work (3x/week)\n";
            plan += "- Low-impact cardio (2x/week)\n";
        } else {
            plan += "- REST & pain management\n";
            plan += "- Consult physician before exercise\n";
        }
        return plan;
    }
}
