// utils/AlertEventManager.java
package utils;

import models.EmergencyAlert;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AlertEventManager {
    private static AlertEventManager instance;
    private final List<Consumer<EmergencyAlert>> listeners = new ArrayList<>();

    private AlertEventManager() {}

    public static AlertEventManager getInstance() {
        if (instance == null) {
            instance = new AlertEventManager();
        }
        return instance;
    }

    public void addListener(Consumer<EmergencyAlert> listener) {
        listeners.add(listener);
    }

    public void triggerAlert(EmergencyAlert alert) {
        for (Consumer<EmergencyAlert> listener : listeners) {
            listener.accept(alert);
        }
    }
}
