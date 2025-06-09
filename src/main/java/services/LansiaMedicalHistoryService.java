package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.LansiaMedicalHistory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LansiaMedicalHistoryService {

    private List<LansiaMedicalHistory> medicalHistoryList = new ArrayList<>();
    private static final String DATA_FILE = "medical_history.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LansiaMedicalHistoryService() {
        loadFromJson();
    }

    public List<LansiaMedicalHistory> getAllHistories() {
        return medicalHistoryList;
    }

    public void addHistory(LansiaMedicalHistory history) {
        history.setHistoryId(UUID.randomUUID());
        medicalHistoryList.add(history);
        saveToJson();
    }

    public void removeHistory(LansiaMedicalHistory history) {
        medicalHistoryList.remove(history);
        saveToJson();
    }

    public void updateHistory(LansiaMedicalHistory updatedHistory) {
        for (int i = 0; i < medicalHistoryList.size(); i++) {
            if (medicalHistoryList.get(i).getHistoryId().equals(updatedHistory.getHistoryId())) {
                medicalHistoryList.set(i, updatedHistory);
                break;
            }
        }
        saveToJson();
    }

    public void saveToJson() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(DATA_FILE), medicalHistoryList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromJson() {
        try {
            File file = new File(DATA_FILE);
            if (file.exists()) {
                medicalHistoryList = objectMapper.readValue(file, new TypeReference<List<LansiaMedicalHistory>>() {});
            } else {
                medicalHistoryList = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            medicalHistoryList = new ArrayList<>();
        }
    }
}
