package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import java.util.List;

public interface IKeyWorkBase {

    String getContent();

    String getFunctionClass();

    String getBatchBomNodeCode();

    String getKeyWorkType();

    List<String> getCarNoList();

    String getPosition();

    String getWorkEnv();

    void setContent(String content);

    void setFunctionClass(String functionClass);

    void setBatchBomNodeCode(String batchBomNodeCode);

    void setKeyWorkType(String keyWorkType);

    void setCarNoList(List<String> carNoList);

    void setPosition(String position);

    void setWorkEnv(String workEnv);

}
