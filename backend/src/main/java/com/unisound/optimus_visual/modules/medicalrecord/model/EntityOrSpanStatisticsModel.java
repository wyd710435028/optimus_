package com.unisound.optimus_visual.modules.medicalrecord.model;

import lombok.Data;

/**
 * entity和span的统计model
 */
@Data
public class EntityOrSpanStatisticsModel {

    String emrNo;
    String docName;
    String nodeName;
    String nodeContent;
    String spanTextContent;
    String spanLabel;
}
