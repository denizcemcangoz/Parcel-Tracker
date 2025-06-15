package structures;

import model.Parcel;

public class LinkedList {
    private Node head;

    public LinkedList() {
        head = null;
    }

    public void add(Parcel parcel) {
        Node newNode = new Node(parcel);
        if (head == null) {
            head = newNode;
        } else {
            Node curr = head;
            while (curr.getNext() != null) {
                curr = curr.getNext();
            }
            curr.setNext(newNode);
        }
    }
    public Node getHead() {
        return head;
    }


    public void remove(Parcel parcel) {
        Node prev = null;
        Node curr = head;
        while (curr != null && !curr.getValue().getParcelID().equals(parcel.getParcelID())) {
            prev = curr;
            curr = curr.getNext();
        }
        if (curr != null) {
            if (prev == null) {
                head = curr.getNext();
            } else {
                prev.setNext(curr.getNext());
            }
        }
    }

    public Node search(String parcelID) {
        Node curr = head;
        while (curr != null && !curr.getValue().getParcelID().equals(parcelID)) {
            curr = curr.getNext();
        }
        return curr;
    }

    public void insert(Parcel parcel, int pos) {
        Node newNode = new Node(parcel);
        if (pos == 0) {
            newNode.setNext(head);
            head = newNode;
        } else {
            Node prev = null;
            Node curr = head;
            for (int i = 0; i < pos && curr != null; i++) {
                prev = curr;
                curr = curr.getNext();
            }
            if (curr != null) {
                prev.setNext(newNode);
                newNode.setNext(curr);
            } else {
                prev.setNext(newNode);
            }
        }
    }

    public void reverse() {
        Node prev = null;
        Node curr = head;
        while (curr != null) {
            Node next = curr.getNext();
            curr.setNext(prev);
            prev = curr;
            curr = next;
        }
        head = prev;
    }

    public int size() {
        int count = 0;
        Node curr = head;
        while (curr != null) {
            count++;
            curr = curr.getNext();
        }
        return count;
    }

    // Node class
    public static class Node {
        private Parcel value;
        private Node next;

        public Node(Parcel value) {
            this.value = value;
            this.next = null;
        }

        public Parcel getValue() {
            return value;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }
    }
}
