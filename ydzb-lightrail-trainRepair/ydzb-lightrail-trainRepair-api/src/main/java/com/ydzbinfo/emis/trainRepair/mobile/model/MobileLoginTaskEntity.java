package com.ydzbinfo.emis.trainRepair.mobile.model;

import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotpacket;

import java.util.List;

public class MobileLoginTaskEntity {

    //车组ID
    private String trainsetID;
    //车组名称
    private String trainsetName;
    //股道列位名称
    private String trackPlaceName;
    //作业包集合
    private  List<XzyMTaskallotpacket>  packetList;

    private String homeAllotTask;

    public String getHomeAllotTask() {
        return homeAllotTask;
    }

    public void setHomeAllotTask(String homeAllotTask) {
        this.homeAllotTask = homeAllotTask;
    }

    public String getTrainsetID() {
        return trainsetID;
    }

    public void setTrainsetID(String trainsetID) {
        this.trainsetID = trainsetID;
    }

    public String getTrainsetName() {
        return trainsetName;
    }

    public void setTrainsetName(String trainsetName) {
        this.trainsetName = trainsetName;
    }

    public String getTrackPlaceName() {
        return trackPlaceName;
    }

    public void setTrackPlaceName(String trackPlaceName) {
        this.trackPlaceName = trackPlaceName;
    }

    public List<XzyMTaskallotpacket> getPacketList() {
        return packetList;
    }

    public void setPacketList(List<XzyMTaskallotpacket> packetList) {
        this.packetList = packetList;
    }
}
