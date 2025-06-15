// ArrivalBuffer.java (Elle yazılmış Queue + Overflow Loglama + getCapacity + visualizeQueue)

package structures;

import model.Parcel;
import utils.Logger;

import java.io.IOException;

public class ArrivalBuffer {

    private static class Node {
        Parcel data;
        Node next;

        Node(Parcel data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node front;
    private Node rear;
    private int size;
    private final int capacity;
    private final Logger logger;

    public ArrivalBuffer(int capacity) {
        this.capacity = capacity;
        this.front = null;
        this.rear = null;
        this.size = 0;

        Logger tempLogger;
        try {
            tempLogger = Logger.getInstance("log.txt", true);
        } catch (IOException e) {
            throw new RuntimeException("Logger init failed", e);
        }
        this.logger = tempLogger;
    }

    public void enqueue(Parcel parcel) {
        if (isFull()) {
            logger.log("WARNING: Queue Overflow! Parcel " + parcel.getParcelID() + " to " + parcel.getDestinationCity() + " discarded.");
            return;
        }

        Node newNode = new Node(parcel);
        if (rear == null) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    public Parcel dequeue() {
        if (isEmpty()) return null;
        Parcel parcel = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return parcel;
    }

    public Parcel peek() {
        return (front != null) ? front.data : null;
    }

    public boolean isFull() {
        return size >= capacity;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }

    public void visualizeQueue() {
        System.out.println("ArrivalBuffer Queue [size=" + size + "/" + capacity + "]:");
        Node current = front;
        while (current != null) {
            Parcel parcel = current.data;
            System.out.print("[" + parcel.getParcelID() + " to " + parcel.getDestinationCity() + "] -> ");
            current = current.next;
        }
        System.out.println("NULL");
    }
}
