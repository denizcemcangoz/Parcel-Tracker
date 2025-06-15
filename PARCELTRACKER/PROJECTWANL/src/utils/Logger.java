// Logger.java
package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static Logger instance;
    private BufferedWriter logWriter;
    private final boolean consoleOutput;

    private Logger(String logFile, boolean consoleOutput) throws IOException {
        this.logWriter = new BufferedWriter(new FileWriter(logFile, true));
        this.consoleOutput = consoleOutput;
        log("=== Simulation started at " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " ===");
    }

    public static synchronized Logger getInstance(String logFile, boolean consoleOutput) throws IOException {
        if (instance == null) {
            instance = new Logger(logFile, consoleOutput);
        }
        return instance;
    }

    public void log(String message) {
        try {
            logWriter.write(message);
            logWriter.newLine();
            logWriter.flush();
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }

        if (consoleOutput) {
            System.out.println(message);
        }
    }

    public void close() {
        try {
            logWriter.close();
        } catch (IOException e) {
            System.err.println("Error closing log file: " + e.getMessage());
        }
    }
}