


package model;

import model.enums.ParcelPriority;
import model.enums.ParcelSize;
import model.enums.ParcelStatus;

public class Parcel {
    private String parcelID;
    private String destinationCity;
    private ParcelPriority priority;
    private ParcelSize size;
    private int arrivalTick;
    private ParcelStatus status;

    public Parcel(String parcelID, String destinationCity, ParcelPriority priority, 
                 ParcelSize size, int arrivalTick) {
        this.parcelID = parcelID;
        this.destinationCity = destinationCity;
        this.priority = priority;
        this.size = size;
        this.arrivalTick = arrivalTick;
        this.status = ParcelStatus.IN_QUEUE;
    }

    // Getters and setters
    public String getParcelID() { return parcelID; }
    public String getDestinationCity() { return destinationCity; }
    public ParcelPriority getPriority() { return priority; }
    public ParcelSize getSize() { return size; }
    public int getArrivalTick() { return arrivalTick; }
    public ParcelStatus getStatus() { return status; }
    public void setStatus(ParcelStatus status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("ParcelID: %s, Destination: %s, Priority: %s, Size: %s, ArrivalTick: %d",
                parcelID, destinationCity, priority, size, arrivalTick);
    }
    
}