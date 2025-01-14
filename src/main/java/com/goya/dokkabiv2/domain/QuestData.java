package com.goya.dokkabiv2.domain;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class QuestData {
    public String index;
    public String questID;
    public String questType;
    public String questName;
    public String questDesc;
}
