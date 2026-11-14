package myweightfitness;

import java.util.ArrayList;

/**
 * CATEGORY: MODEL
 * Parent class of every user. Stores the profile data (Encapsulation)
 * and the CRUD methods from the proposal: addUser(), editUser(), deleteUser().
 */
public class User {
    // Encapsulation - private attributes
    private String name;
    private int age;
    private double height;        // in cm
    private double currentWeight; // in kg
    private double targetWeight;  // in kg
    private String category;      // Gym Enthusiast, Athlete, Rehabilitation Patient
    private String fitnessLevel;  // Beginner, Intermediate, Advanced
    private double bmi;
    private String gender;        // "Male", "Female" or "" (not specified)
    private int timeframeMonths;

    // All registered users (ArrayList) - used by addUser / editUser / deleteUser
    private static ArrayList<User> users = new ArrayList<>();

    public User() {
        this.name = "Unknown";
        this.age = 0;
        this.height = 0;
        this.currentWeight = 0.0;
        this.targetWeight = 0.0;
        this.category = "General";
        this.fitnessLevel = "Beginner";
        this.bmi = 0;
        this.gender = "Not Specified";
        this.timeframeMonths = 1;
    }

    // Parameterized Constructor (Easier instantiation)
    public User(String name, int age, double height, double currentWeight, double targetWeight, String category, String fitnessLevel) {
        this.name = name;
        this.age = age;
        this.height = height;
        this.currentWeight = currentWeight;
        this.targetWeight = targetWeight;
        this.category = category;
        this.fitnessLevel = fitnessLevel;
        this.gender = "Not Specified";
        this.timeframeMonths = 1;
    }

    // ================= Getters and Setters (Encapsulation) =================
    public String getName() { return name; }
    public void setName(String name) { 
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim(); 
        }
    }

    public int getAge() { return age; }
    public void setAge(int age) { 
        if (age > 0 && age < 120) { // Basic validation
            this.age = age; 
        }
    }

    public double getHeight() { return height; }
    public void setHeight(double height) { 
        if (height > 0) {
            this.height = height; 
        }
    }

    public double getCurrentWeight() { return currentWeight; }
    public void setCurrentWeight(double weight) { 
        if (weight > 0) {
            this.currentWeight = weight; 
        }
    }

    public double getTargetWeight() { return targetWeight; }
    public void setTargetWeight(double weight) { 
        if (weight > 0) {
            this.targetWeight = weight; 
        }
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { 
        this.category = (category == null) ? "General" : category; 
    }

    public String getFitnessLevel() { return fitnessLevel; }
    public void setFitnessLevel(String level) { 
        this.fitnessLevel = (level == null) ? "Beginner" : level; 
    }

    public String getGender() { return gender; }
    public void setGender(String gender) { 
        this.gender = (gender == null || gender.trim().isEmpty()) ? "Not Specified" : gender; 
    }
    
    public int getTimeframeMonths() { return timeframeMonths; }
    public void setTimeframeMonths(int months) { 
        if (months > 0) {
            this.timeframeMonths = months; 
        }
    }

    // ================= BMI =================
    public double calculateBMI() {
        if (height <= 0) {
            bmi = 0;
            return 0;
        }
        double heightMeters = height / 100.0;
        bmi = currentWeight / (heightMeters * heightMeters);
        return bmi;
    }

    public String getBMIStatus() {
        double value = calculateBMI();
        if (value < 18.5) return "Underweight";
        if (value < 25) return "Normal";
        if (value < 30) return "Overweight";
        return "Obese";
    }

    // ================= Polymorphism hooks =================
    // Each subclass (GymUser, Athlete, RehabPatient) overrides these.
    public String generateWorkoutPlan() {
        return "No plan available.";
    }

    public boolean isReadyToTrain() {
        return true;
    }

    // ================= CRUD (from the proposal) =================
    /** Create: register a new user. */
    public static void addUser(User user) {
        if (user != null) {
            users.add(user);
        }
    }

    /** Update: edit the profile of the user with the given name. */
    public static boolean editUser(String name, String newName, int age,
                                   double height, double currentWeight, double targetWeight) {
        User user = findUser(name);
        if (user == null) return false;
        user.setName(newName);
        user.setAge(age);
        user.setHeight(height);
        user.setCurrentWeight(currentWeight);
        user.setTargetWeight(targetWeight);
        return true;
    }

    /** Delete: remove the user with the given name. */
    public static boolean deleteUser(String name) {
        User user = findUser(name);
        return user != null && users.remove(user);
    }

    /** Read: find one user by name (not case sensitive). */
    public static User findUser(String name) {
        if (name == null) return null;
        for (User u : users) {
            if (u.getName().equalsIgnoreCase(name.trim())) {
                return u;
            }
        }
        return null;
    }

    /** Read: all registered users (a copy, so the list stays protected). */
    public static ArrayList<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}