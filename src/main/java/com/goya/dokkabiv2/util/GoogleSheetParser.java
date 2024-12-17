package com.goya.dokkabiv2.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.List;

public class GoogleSheetParser {

    private final Sheets sheetsService;

    public GoogleSheetParser(GoogleCredential credential) throws IOException {
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