package com.ydzbinfo.emis.utils;

import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.PacketInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 以使用线程缓存的方式获取数据
 *
 * @author 张天可
 **/
@Component
public class RemoteServiceCachedDataUtil {

    private static IRemoteService remoteService;

    @Autowired
    public void setRemoteService(IRemoteService remoteService) {
        RemoteServiceCachedDataUtil.remoteService = remoteService;
    }

    public static List<PacketInfo> getPacketList() {
        return CacheUtil.getDataUseThreadCache(
            "remoteService.getPacketList",
            () -> remoteService.getPacketList()
        );
    }

    public static List<TrainsetBaseInfo> getTrainsetList() {
        return CacheUtil.getDataUseThreadCache(
            "remoteService.getTrainsetList",
            () -> remoteService.getTrainsetList()
        );
    }

    public static TrainsetBaseInfo getTrainsetBaseinfoByID(String trainsetId) {
        return CacheUtil.getDataUseThreadCache(
            "remoteService.getTrainsetBaseinfoByID_" + trainsetId,
            () -> remoteService.getTrainsetBaseinfoByID(trainsetId)
        );
    }
}
