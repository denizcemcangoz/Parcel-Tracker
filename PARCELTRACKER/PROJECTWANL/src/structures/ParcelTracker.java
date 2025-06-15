// ParcelTracker.java
//hash table
// ParcelTracker.java
package structures;

import model.Parcel;
import model.enums.ParcelStatus;
import java.util.ArrayList;
import java.util.List;
import model.enums.ParcelPriority;
import model.enums.ParcelSize;

public class ParcelTracker {
    private static class HashNode {
        String parcelID;
        ParcelStatus status;
        int arrivalTick;
        Integer dispatchTick;
        int returnCount;
        String destinationCity;
        int priority;
        String size;
        HashNode next;

        HashNode(String parcelID, Parcel parcel) {
            this.parcelID = parcelID;
            this.status = parcel.getStatus();
            this.arrivalTick = parcel.getArrivalTick();
            this.dispatchTick = null;
            this.returnCount = 0;
            this.destinationCity = parcel.getDestinationCity();
            this.priority = parcel.getPriority().getValue();
            this.size = parcel.getSize().toString();
            this.next = null;
        }
    }

    private int capacity;  // Removed 'final' modifier
    private final List<HashNode> buckets;
    private int size;
    private final double loadFactorThreshold = 0.75;

    public ParcelTracker(int initialCapacity) {
        this.capacity = initialCapacity;  // Now we can modify this
        this.buckets = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            buckets.add(null);
        }
        this.size = 0;
    }

    private int hash(String key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    public void insert(String parcelID, Parcel parcel) {
        if (exists(parcelID)) {
            return;
        }

        int bucketIndex = hash(parcelID);
        HashNode newNode = new HashNode(parcelID, parcel);
        HashNode head = buckets.get(bucketIndex);

        if (head == null) {
            buckets.set(bucketIndex, newNode);
        } else {
            newNode.next = head;
            buckets.set(bucketIndex, newNode);
        }
        size++;

        // Check load factor and resize if needed
        if ((1.0 * size) / capacity >= loadFactorThreshold) {
            resize();
        }
    }

    private void resize() {
        List<HashNode> temp = buckets;
        buckets.clear();
        capacity *= 2;
        for (int i = 0; i < capacity; i++) {
            buckets.add(null);
        }
        size = 0;

        for (HashNode head : temp) {
            while (head != null) {
                insert(head.parcelID, createParcelFromNode(head));
                head = head.next;
            }
        }
    }

    private Parcel createParcelFromNode(HashNode node) {
        // This is a simplified version - in a real implementation, you'd need to map back to enums
        return new Parcel(node.parcelID, node.destinationCity,
                        ParcelPriority.values()[node.priority - 1],
                        ParcelSize.valueOf(node.size.toUpperCase()),
                        node.arrivalTick);
    }

    public void updateStatus(String parcelID, ParcelStatus newStatus) {
        HashNode node = getNode(parcelID);
        if (node != null) {
            node.status = newStatus;
        }
    }

    public HashNode get(String parcelID) {
        return getNode(parcelID);
    }

    private HashNode getNode(String parcelID) {
        int bucketIndex = hash(parcelID);
        HashNode head = buckets.get(bucketIndex);

        while (head != null) {
            if (head.parcelID.equals(parcelID)) {
                return head;
            }
            head = head.next;
        }
        return null;
    }

    public void incrementReturnCount(String parcelID) {
        HashNode node = getNode(parcelID);
        if (node != null) {
            node.returnCount++;
        }
    }

    public boolean exists(String parcelID) {
        return getNode(parcelID) != null;
    }

    public int size() {
        return size;
    }

    public double getLoadFactor() {
        return (double) size / capacity;
    }

}