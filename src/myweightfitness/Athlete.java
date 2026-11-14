package myweightfitness;

/**
 * CATEGORY: MODEL
 * Athlete. Inherits from User and overrides generateWorkoutPlan().
 */
public class Athlete extends User {
    private String sport;

    public Athlete() {
        super();
        setCategory("Athlete");
        this.sport = "General";
    }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    @Override
    public String generateWorkoutPlan() {
        return "SPORT-SPECIFIC TRAINING (" + sport + ")\n"
             + "- Sport-specific drills (4x/week)\n"
             + "- Conditioning (3x/week)\n"
             + "- Recovery sessions (2x/week)\n";
    }
}
