package com.ydzbinfo.emis.trainRepair.faultconfig.util;

import com.ydzbinfo.emis.trainRepair.faultconfig.querymodel.FaultInputDict;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParamUtil;

import java.util.List;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;
import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.likeIgnoreCaseParam;

/**
 * @author 张天可
 * @since 2021/12/8
 */
public class FaultUtil {

    public static List<ColumnParam<FaultInputDict>> toColumnParamList(FaultInputDict model) {
        return ColumnParamUtil.filterBlankParamList(
            eqParam(FaultInputDict::getPartCode, model.getPartCode()),
            eqParam(FaultInputDict::getFaultLevelCdoe, model.getFaultLevelCdoe()),
            eqParam(FaultInputDict::getFlag, model.getFlag()),
            likeIgnoreCaseParam(FaultInputDict::getKey, model.getKey()),
            likeIgnoreCaseParam(FaultInputDict::getFaultModeName, model.getFaultModeName()),
            likeIgnoreCaseParam(FaultInputDict::getFunctionTypeName, model.getFunctionTypeName())
        );
    }
}
