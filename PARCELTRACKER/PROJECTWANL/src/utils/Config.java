// Config.java
package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import structures.HashTable; // bizim elle yazdığımız HashTable'ı import ediyoruz

public class Config {
    private final HashTable<String, String> configMap;

    public Config(String configFile) {
        this.configMap = new HashTable<>(100); // kapasiteyi 100 verdim, değiştirebilirsin
        loadConfig(configFile);
    }

    private void loadConfig(String configFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    configMap.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading config file: " + e.getMessage());
        }
    }

    public int getInt(String key, int defaultValue) {
        String value = configMap.get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public double getDouble(String key, double defaultValue) {
        String value = configMap.get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public String getString(String key, String defaultValue) {
        String value = configMap.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public String[] getStringArray(String key, String[] defaultValue) {
        String value = configMap.get(key);
        if (value == null) {
            return defaultValue;
        }
        return value.split("\\s*,\\s*");
    }
}
