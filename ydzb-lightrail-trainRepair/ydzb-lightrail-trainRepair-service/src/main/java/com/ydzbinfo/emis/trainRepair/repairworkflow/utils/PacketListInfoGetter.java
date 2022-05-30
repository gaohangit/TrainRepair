package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import com.ydzbinfo.emis.utils.PacketInfoUtils;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.function.Supplier;

/**
 * 包列表信息包装类
 *
 * @author 张天可
 **/
@AllArgsConstructor
class PacketListInfoGetter {
    private final Supplier<List<PacketInfoUtils.PacketInfoGetter>> packetsGetter;

    List<PacketInfoUtils.PacketInfoGetter> getPackets() {
        return packetsGetter.get();
    }
}
