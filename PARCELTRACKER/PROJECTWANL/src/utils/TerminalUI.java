// TerminalUI.java
package utils;

import structures.*;

public class TerminalUI {
    public static void displayDashboard(ArrivalBuffer buffer, DestinationSorter sorter, 
                                      ReturnStack stack, TerminalRotator rotator) {
        clearScreen();
        displayHeader();
        buffer.visualizeQueue();
        sorter.visualizeBST();
        displayStack(stack);
        displayTerminal(rotator);
        displaySeparator();
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void displayHeader() {
        System.out.println("=== PARCEL SORTX SIMULATION ===");
        System.out.println("┌──────────────────────────────┐");
        System.out.println("│      LOGISTICS CENTER       │");
        System.out.println("└──────────────────────────────┘");
    }

    private static void displayStack(ReturnStack stack) {
        System.out.println("\nReturn Stack:");
        System.out.println("  ┌─────┐");
        if (stack.size() > 0) {
            System.out.println("  │ " + stack.peek().getParcelID() + " │");
        } else {
            System.out.println("  │ EMPTY │");
        }
        System.out.println("  └─────┘");
        System.out.println("Size: " + stack.size());
    }

    private static void displayTerminal(TerminalRotator rotator) {
        System.out.println("\nActive Terminal: [" + rotator.getActiveTerminal() + "]");
        System.out.println("  ╔══════════════╗");
        System.out.println("  ║   DISPATCH   ║");
        System.out.println("  ╚══════════════╝");
    }

    private static void displaySeparator() {
        System.out.println("\n──────────────────────────────");
    }
}