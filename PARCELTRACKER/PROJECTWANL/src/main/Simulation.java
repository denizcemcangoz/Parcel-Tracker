package main;

import model.Parcel;
import model.enums.ParcelPriority;
import model.enums.ParcelSize;
import model.enums.ParcelStatus;
import structures.*;
import utils.Config;
import utils.Logger;
import utils.TerminalUI;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Simulation {
    private final Config config;
    private final Logger logger;
    private final Random random;
    
    private int currentTick;
    private int totalParcelsGenerated;
    private int totalParcelsDispatched;
    private int totalParcelsReturned;
    
    private final ArrivalBuffer arrivalBuffer;
    private final ReturnStack returnStack;
    private final DestinationSorter destinationSorter;
    private final ParcelTracker parcelTracker;
    private final TerminalRotator terminalRotator;
    
    public Simulation(String configFile) throws IOException {
        this.config = new Config(configFile);
        this.logger = Logger.getInstance("log.txt", true);
        this.random = new Random();
        
        // Initialize data structures
        int queueCapacity = config.getInt("QUEUE_CAPACITY", 30);
        this.arrivalBuffer = new ArrivalBuffer(queueCapacity);
        this.returnStack = new ReturnStack();
        this.destinationSorter = new DestinationSorter();
        this.parcelTracker = new ParcelTracker(100);
        
        String[] cities = config.getStringArray("CITY_LIST", new String[]{"Istanbul", "Ankara", "Izmir"});
        this.terminalRotator = new TerminalRotator(cities);
        
        this.currentTick = 0;
        this.totalParcelsGenerated = 0;
        this.totalParcelsDispatched = 0;
        this.totalParcelsReturned = 0;
    }
    
    public void run() {
        int maxTicks = config.getInt("MAX_TICKS", 300);
        Boolean useTerminalUI = true; // Set to false to disable UI
        
        while (currentTick < maxTicks) {
            currentTick++;
            if (useTerminalUI) {
            TerminalUI.displayDashboard(arrivalBuffer, destinationSorter, returnStack, terminalRotator);
        }

            logger.log("\n[Tick " + currentTick + "]");
            
            // 1. Generate new parcels
            generateParcels();
            
            // 2. Process arrival buffer
            processArrivalBuffer();
            
            // 3. Dispatch parcels for current terminal
            dispatchParcels();
            
            // 4. Process return stack (every 3 ticks)
            if (currentTick % 3 == 0) {
                processReturnStack();
            }
            
            // 5. Rotate terminal if needed
            if (currentTick % config.getInt("TERMINAL_ROTATION_INTERVAL", 5) == 0) {
                terminalRotator.advanceTerminal();
                logger.log("Rotated to: " + terminalRotator.getActiveTerminal());
            }
            
            // Log summary for the tick
            logTickSummary();
            try {
                Thread.sleep(0); // 0.5 second between ticks
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Generate final report
        generateFinalReport();
        logger.close();
    }
    
    private void generateParcels() {
        int minParcels = config.getInt("PARCEL_PER_TICK_MIN", 1);
        int maxParcels = config.getInt("PARCEL_PER_TICK_MAX", 3);
        int numParcels = minParcels + random.nextInt(maxParcels - minParcels + 1);
        
        if (numParcels > 0) {
            logger.log("New Parcels:");
        }
        
        for (int i = 0; i < numParcels; i++) {
            String parcelID = "P" + (1000 + totalParcelsGenerated + i);
            String[] cities = config.getStringArray("CITY_LIST", new String[]{"Istanbul", "Ankara", "Izmir"});
            String destination = cities[random.nextInt(cities.length)];
            ParcelPriority priority = ParcelPriority.values()[random.nextInt(3)];
            ParcelSize size = ParcelSize.values()[random.nextInt(3)];
            
            Parcel parcel = new Parcel(parcelID, destination, priority, size, currentTick);
            arrivalBuffer.enqueue(parcel);
            parcelTracker.insert(parcelID, parcel);
            
            logger.log("  " + parcelID + " to " + destination + " (Priority " + priority.getValue() + ")");
            totalParcelsGenerated++;
        }
        
        logger.log("Queue Size: " + arrivalBuffer.size());
    }
    
    private void processArrivalBuffer() {
        if (!arrivalBuffer.isEmpty()) {
            Parcel parcel = arrivalBuffer.dequeue();
            if (parcel != null) {
                destinationSorter.insertParcel(parcel);
                parcel.setStatus(ParcelStatus.SORTED);
                parcelTracker.updateStatus(parcel.getParcelID(), ParcelStatus.SORTED);
                logger.log("Sorted to BST: " + parcel.getParcelID() + " to " + parcel.getDestinationCity());
            }
        }
    }

    private void dispatchParcels() {
        String activeTerminal = terminalRotator.getActiveTerminal();
        LinkedList cityParcels = destinationSorter.getCityParcels(activeTerminal);

        if (cityParcels != null && cityParcels.size() > 0) {
            // Priority-based selection
            Parcel parcelToDispatch = selectByPriority(cityParcels);

            double misrouteRate = config.getDouble("MISROUTING_RATE", 0.1);
            boolean misrouted = random.nextDouble() < misrouteRate;

            if (misrouted) {
                returnStack.push(parcelToDispatch);
                parcelToDispatch.setStatus(ParcelStatus.RETURNED);
                parcelTracker.updateStatus(parcelToDispatch.getParcelID(), ParcelStatus.RETURNED);
                parcelTracker.incrementReturnCount(parcelToDispatch.getParcelID());
                logger.log("Returned: " + parcelToDispatch.getParcelID() + " misrouted -> Pushed to ReturnStack");
                totalParcelsReturned++;
            } else {
                destinationSorter.removeParcel(activeTerminal, parcelToDispatch.getParcelID());
                parcelToDispatch.setStatus(ParcelStatus.DISPATCHED);
                parcelTracker.updateStatus(parcelToDispatch.getParcelID(), ParcelStatus.DISPATCHED);
                logger.log("Dispatched: " + parcelToDispatch.getParcelID() + " from BST to " + activeTerminal + " -> Success");
                totalParcelsDispatched++;
            }
        }
    }

    private Parcel selectByPriority(LinkedList parcels) {
        // Check for high priority parcels first
        LinkedList.Node current = parcels.getHead();
        while (current != null) {
            Parcel p = current.getValue();
            if (p.getPriority() == ParcelPriority.HIGH) {
                return p;
            }
            current = current.getNext();
        }

        // Then medium priority
        current = parcels.getHead();
        while (current != null) {
            Parcel p = current.getValue();
            if (p.getPriority() == ParcelPriority.MEDIUM) {
                return p;
            }
            current = current.getNext();
        }

        // Default to first in list (FIFO) if no priorities found
        if (parcels.getHead() != null) {
            return parcels.getHead().getValue();
        }

        return null; // fallback (should not happen)
    }

    

    
    private void processReturnStack() {
        if (!returnStack.isEmpty()) {
            Parcel parcel = returnStack.pop();
            if (parcel != null) {
                destinationSorter.insertParcel(parcel);
                parcel.setStatus(ParcelStatus.SORTED);
                parcelTracker.updateStatus(parcel.getParcelID(), ParcelStatus.SORTED);
                logger.log("Reprocessed from ReturnStack: " + parcel.getParcelID());
            }
        }
    }
    
    private void logTickSummary() {
        logger.log("Active Terminal: " + terminalRotator.getActiveTerminal());
        logger.log("ReturnStack Size: " + returnStack.size());
        logger.log("BST Stats: Nodes=" + destinationSorter.getNodeCount() + ", Height=" + destinationSorter.getHeight());

    }
    
    private void generateFinalReport() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("report.txt"))) {
            writer.write("=== ParcelSortX Simulation Report ===\n");
            writer.write("Generated at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");
            
            writer.write("1. Simulation Overview\n");
            writer.write("   - Total Ticks Executed: " + currentTick + "\n");
            writer.write("   - Number of Parcels Generated: " + totalParcelsGenerated + "\n\n");
            
            writer.write("2. Parcel Statistics\n");
            writer.write("   - Total Dispatched Parcels: " + totalParcelsDispatched + "\n");
            writer.write("   - Total Returned Parcels: " + totalParcelsReturned + "\n");
            writer.write("   - Parcels in Queue at End: " + arrivalBuffer.size() + "\n");
            writer.write("   - Parcels in BST at End: " + getTotalParcelsInBST() + "\n");
            writer.write("   - Parcels in ReturnStack at End: " + returnStack.size() + "\n\n");
            
            writer.write("3. Data Structure Statistics\n");
            writer.write("   - Maximum Queue Size Observed: " + arrivalBuffer.getCapacity() + "\n");
            writer.write("   - Maximum Stack Size Observed: " + returnStack.getMaxSize() + "\n");
            writer.write("   - Final Height of BST: " + destinationSorter.getHeight() + "\n");
            writer.write("   - Hash Table Load Factor: " + String.format("%.2f", parcelTracker.getLoadFactor()) + "\n");
            
            // Add more statistics as needed
        } catch (IOException e) {
            System.err.println("Error writing final report: " + e.getMessage());
        }
    }
    
    private int getTotalParcelsInBST() {
        // This would require traversing the BST and summing up all parcels in all city nodes
        return 0; // Placeholder
    }
    
    private int getMaxStackSize() {
        // This would require tracking the maximum size during the simulation
        return returnStack.size(); // Placeholder
    }
    
    public static void main(String[] args) {
        try {
            Simulation simulation = new Simulation("config.txt");
            simulation.run();
        } catch (IOException e) {
            System.err.println("Failed to initialize simulation: " + e.getMessage());
        }
    }
}