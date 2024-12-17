package com.goya.dokkabiv2.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SheetToJsonConverter {

    // 데이터를 객체로 변환
    public static <T> List<T> parseSheetData(Class<T> clazz, List<List<Object>> sheetData) throws Exception {
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
            for (int j = 0; j < headers.size(); j++) {
                String fieldName = headers.get(j);
                if (j < row.size()) {
                    Object value = row.get(j);
                    clazz.getDeclaredField(fieldName).setAccessible(true);
                    clazz.getDeclaredField(fieldName).set(instance, value);
                }
            }
            result.add(instance);
        }
        return result;
    }

    // 객체를 JSON 파일로 저장
    public static <T> void saveAsJson(List<T> data, String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(filePath), data);
    }
}