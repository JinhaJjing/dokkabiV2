package com.goya.dokkabiv2.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class GoogleSheetParser {

    @Value("${GOOGLE_SHEET_CREDENTIALS_PATH}")
    private Resource credentialsResource;

    private Sheets sheetsService;

    @PostConstruct
    public void init() throws IOException {
        // 리소스에서 InputStream 가져오기
        InputStream inputStream = credentialsResource.getInputStream();

        // GoogleCredential 객체 생성
        GoogleCredential credential = GoogleCredential.fromStream(inputStream)
                .createScoped(List.of("https://www.googleapis.com/auth/spreadsheets"));

        this.sheetsService = new Sheets.Builder(
                credential.getTransport(),
                credential.getJsonFactory(),
                credential
        ).setApplicationName("Google Sheets Parser").build();
    }

    public List<List<Object>> getSheetData(String spreadsheetId, String sheetName) throws IOException {
        String range = sheetName + "!A:Z";
        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        return response.getValues();
    }
}