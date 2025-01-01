package com.goya.dokkabiv2.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goya.dokkabiv2.domain.MapData;
import com.goya.dokkabiv2.domain.QuestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Component
public class GoogleSheetToJsonConverter implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(GoogleSheetToJsonConverter.class);
    @Value("${google.sheetId}")
    private String spreadsheetId;

    private final GoogleSheetParser parser;

    public GoogleSheetToJsonConverter(GoogleSheetParser parser) {
        this.parser = parser;
    }

    @Override
    public void run(String... args) {
        // 시트 데이터를 객체로 변환하고 data 폴더에 JSON 파일로 저장
        convertAndSaveSheetData("Quest", QuestData.class, "quest_data.json");
        convertAndSaveSheetData("Map", MapData.class, "map_data.json");
    }

    private <T> void convertAndSaveSheetData(String sheetName, Class<T> dataClass, String outputFileName) {
        File jsonFile = new File("src/main/resources/data/" + outputFileName);
        if (jsonFile.exists()) {
            log.info("{} 파일이 이미 존재합니다. 생성을 생략합니다.", outputFileName);
            return;
        }
        log.info("{} 파일이 존재하지 않아 생성합니다.", outputFileName);

        try {
            List<List<Object>> sheetData = parser.getSheetData(spreadsheetId, sheetName);
            List<T> dataList = parseSheetData(dataClass, sheetData);
            saveAsJson(dataList, outputFileName);
            log.info("{} 시트의 데이터가 {} 파일에 저장되었습니다.", sheetName, outputFileName);
        } catch (Exception e) {
            log.error("Failed to convert sheet data to JSON", e);
        }
    }

    // 데이터를 객체로 변환
    public <T> List<T> parseSheetData(Class<T> clazz, List<List<Object>> sheetData) throws Exception {
        if (sheetData == null || sheetData.isEmpty()) {
            throw new IllegalArgumentException("시트 데이터가 비어있습니다.");
        }

        // 첫 번째 행은 필드 이름으로 사용
        List<String> headers = new ArrayList<>();
        for (Object header : sheetData.get(0)) {
            headers.add(header.toString());
        }

        // 데이터를 객체 리스트로 변환
        List<T> result = new ArrayList<>();
        for (int i = 1; i < sheetData.size(); i++) {
            List<Object> row = sheetData.get(i);
            T instance = clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                String fieldName = field.getName();
                if (headers.contains(fieldName)) {
                    int columnIndex = headers.indexOf(fieldName);
                    if (columnIndex < row.size()) {
                        Object value = row.get(columnIndex);
                        field.setAccessible(true);
                        field.set(instance, value);
                    }
                }
            }
            result.add(instance);
        }
        return result;
    }

    // 객체를 JSON 파일로 저장
    public <T> void saveAsJson(List<T> data, String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("src/main/resources/data/" + filePath), data);
    }
}