package com.goya.dokkabiv2.domain;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class QuestData {
    public String Index;
    public String QuestID;
    public String QuestType;
    public String QuestName;
    public String QuestDesc;
}
