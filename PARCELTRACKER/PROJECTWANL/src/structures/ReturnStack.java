// ReturnStack.java
package structures;

import model.Parcel;

public class ReturnStack {
    private static class Node {
        Parcel parcel;
        Node next;

        Node(Parcel parcel) {
            this.parcel = parcel;
            this.next = null;
        }
    }

    private Node top;
    private int size;
    private int maxSize = 0;

    public ReturnStack() {
        this.top = null;
        this.size = 0;
    }

    public void push(Parcel parcel) {
        Node newNode = new Node(parcel);
        newNode.next = top;
        top = newNode;
        size++;

        // Update maxSize
        if (size > maxSize) {
            maxSize = size;
        }
    }

    public Parcel pop() {
        if (isEmpty()) {
            return null;
        }

        Parcel parcel = top.parcel;
        top = top.next;
        size--;
        return parcel;
    }

    public Parcel peek() {
        if (isEmpty()) {
            return null;
        }
        return top.parcel;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }
    public int getMaxSize() {
        return maxSize;
    }

}
