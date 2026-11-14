package myweightfitness;

import java.util.ArrayList;

/**
 * CATEGORY: MODEL
 * Equipment the user has. Each Equipment object has a name and an availability.
 * The static methods (addEquipment, removeEquipment, viewEquipment) manage the
 * list of equipment the user selected.
 */
public class Equipment {
    private String equipmentName;
    private boolean availability;

    // Every piece of equipment the app knows about (ArrayList)
    private static ArrayList<Equipment> inventory = new ArrayList<>();

    static {
        String[] defaults = {"Dumbbell", "Bench", "Resistance Band", "Barbell", "Pull-Up Bar"};
        for (String name : defaults) {
            inventory.add(new Equipment(name, false)); // nothing selected at the start
        }
    }

    public Equipment(String equipmentName, boolean availability) {
        this.equipmentName = equipmentName;
        this.availability = availability;
    }

    public String getEquipmentName() { return equipmentName; }
    public boolean isAvailable() { return availability; }
    public void setAvailability(boolean availability) { this.availability = availability; }

    /** The user has this equipment (adds it if it is new). */
    public static void addEquipment(String name) {
        if (name == null || name.trim().isEmpty()) return;
        Equipment equipment = find(name);
        if (equipment == null) {
            inventory.add(new Equipment(name.trim(), true));
        } else {
            equipment.availability = true;
        }
    }

    /** The user no longer has this equipment. */
    public static boolean removeEquipment(String name) {
        Equipment equipment = find(name);
        if (equipment == null) return false;
        equipment.availability = false;
        return true;
    }

    /** All equipment with its availability (a copy of the list). */
    public static ArrayList<Equipment> viewEquipment() {
        return new ArrayList<>(inventory);
    }

    /** Only the names of the equipment the user has. */
    public static ArrayList<String> getAvailableEquipmentNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Equipment e : inventory) {
            if (e.availability) names.add(e.equipmentName);
        }
        return names;
    }

    private static Equipment find(String name) {
        if (name == null) return null;
        for (Equipment e : inventory) {
            if (e.equipmentName.equalsIgnoreCase(name.trim())) return e;
        }
        return null;
    }

    @Override
    public String toString() {
        return (availability ? "[✓] " : "[✗] ") + equipmentName;
    }
}