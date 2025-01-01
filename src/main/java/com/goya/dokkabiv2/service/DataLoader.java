package com.goya.dokkabiv2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goya.dokkabiv2.domain.MapData;
import com.goya.dokkabiv2.domain.QuestData;
import com.goya.dokkabiv2.util.GoogleSheetToJsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DataLoader {
    private static final Logger log = LoggerFactory.getLogger(GoogleSheetToJsonConverter.class);
    private List<QuestData> questDataList;
    private List<MapData> mapDataList;

    public void loadData() {
        try {
            QuestData[] questDataArray = new ObjectMapper().readValue(
                    new File("src/main/resources/data/quest_data.json"), QuestData[].class);
            this.questDataList = Arrays.asList(questDataArray);
            log.info("Quest data loaded: {} quests", questDataList.size());

            MapData[] mapDataArray = new ObjectMapper().readValue(
                    new File("src/main/resources/data/map_data.json"), MapData[].class);
            this.mapDataList = Arrays.asList(mapDataArray);
            log.info("Map data loaded: {} maps", mapDataList.size());
        } catch (IOException e) {
            log.error("Error reading data files", e);
        }
    }
}