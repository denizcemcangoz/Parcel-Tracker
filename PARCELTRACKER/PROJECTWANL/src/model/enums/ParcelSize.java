// ParcelSize.java
package model.enums;

public enum ParcelSize {
    SMALL("Small"), 
    MEDIUM("Medium"), 
    LARGE("Large");

    private final String displayName;

    private ParcelSize(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}