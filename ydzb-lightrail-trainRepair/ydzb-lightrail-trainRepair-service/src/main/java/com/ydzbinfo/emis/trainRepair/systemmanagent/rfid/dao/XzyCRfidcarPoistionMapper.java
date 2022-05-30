package com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.model.RfidPosition;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.querymodel.XzyCRfidcarPoistion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XzyCRfidcarPoistionMapper extends BaseMapper<XzyCRfidcarPoistion> {

    List<RfidPosition> selectRfIdPosition(@Param("tid") String tid,
                            @Param("trackCode") String trackCode,
                            @Param("placeCode") String placeCode,
                            @Param("carCount") Integer carCount,
                            @Param("page") Page<RfidPosition> page);
}
