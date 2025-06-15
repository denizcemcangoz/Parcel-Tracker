// TerminalRotator.java
//circular linked list
package structures;

public class TerminalRotator {
    private static class TerminalNode {
        String cityName;
        TerminalNode next;

        TerminalNode(String cityName) {
            this.cityName = cityName;
            this.next = null;
        }
    }

    private TerminalNode head;
    private TerminalNode currentActiveTerminal;
    private int size;

    public TerminalRotator(String[] cities) {
        if (cities == null || cities.length == 0) {
            throw new IllegalArgumentException("City list cannot be empty");
        }

        // Create the circular linked list
        head = new TerminalNode(cities[0]);
        TerminalNode current = head;
        size = 1;

        for (int i = 1; i < cities.length; i++) {
            current.next = new TerminalNode(cities[i]);
            current = current.next;
            size++;
        }

        // Make it circular
        current.next = head;
        currentActiveTerminal = head;
    }

    public void advanceTerminal() {
        if (currentActiveTerminal != null) {
            currentActiveTerminal = currentActiveTerminal.next;
        }
    }

    public String getActiveTerminal() {
        return currentActiveTerminal != null ? currentActiveTerminal.cityName : null;
    }

    public void printTerminalOrder() {
        if (head == null) {
            return;
        }

        TerminalNode current = head;
        do {
            System.out.print(current.cityName);
            if (current == currentActiveTerminal) {
                System.out.print(" (Active)");
            }
            System.out.print(" -> ");
            current = current.next;
        } while (current != head);
        System.out.println("...");
    }

    public int size() {
        return size;
    }
}