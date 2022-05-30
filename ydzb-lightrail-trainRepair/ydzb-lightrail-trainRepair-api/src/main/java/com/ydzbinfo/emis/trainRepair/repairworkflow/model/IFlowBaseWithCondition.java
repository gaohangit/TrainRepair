package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.IBaseFlow;

import java.util.List;

/**
 * @author 张天可
 * @since 2021/7/7
 */
public interface IFlowBaseWithCondition extends IBaseFlow {
    List<String> getTrainTypes();

    Boolean getTrainTypeExclude();

    List<String> getTrainTemplates();

    Boolean getTrainTemplateExclude();

    Boolean getTrainsetIdExclude();

    List<String> getTrainsetIds();

    List<String> getKeyWords();

    String getRepairType();

    Boolean getUsable();

    Boolean getDefaultType();

    String getConditionId();

    void setTrainTypes(java.util.List<String> trainTypes);

    void setTrainTypeExclude(Boolean trainTypeExclude);

    void setTrainTemplates(java.util.List<String> trainTemplates);

    void setTrainTemplateExclude(Boolean trainTemplateExclude);

    void setTrainsetIds(java.util.List<String> trainsetIds);

    void setTrainsetIdExclude(Boolean trainsetIdExclude);

    void setKeyWords(java.util.List<String> keyWords);

    void setRepairType(String repairType);

    void setUsable(Boolean usable);

    void setDefaultType(Boolean defaultType);

    void setConditionId(String conditionId);
}
