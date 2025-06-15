// ParcelPriority.java
package model.enums;

public enum ParcelPriority {
    LOW(1), 
    MEDIUM(2), 
    HIGH(3);

    private final int value;

    ParcelPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ParcelPriority fromValue(int value) {
        for (ParcelPriority priority : ParcelPriority.values()) {
            if (priority.getValue() == value) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid priority value: " + value);
    }
}