// ParcelStatus.java
package model.enums;

public enum ParcelStatus {
    IN_QUEUE("InQueue"),
    SORTED("Sorted"),
    DISPATCHED("Dispatched"),
    RETURNED("Returned");

    private final String status;

    private ParcelStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}