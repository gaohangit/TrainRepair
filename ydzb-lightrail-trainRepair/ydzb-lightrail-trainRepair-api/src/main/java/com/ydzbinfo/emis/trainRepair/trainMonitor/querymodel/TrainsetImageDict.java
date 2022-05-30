package com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableField;
import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author gaohan
 * @since 2021-03-02
 */
@TableName("XZY_B_TRAINSETIMAGE_DICT")
public class TrainsetImageDict implements Serializable {

    @TableField("S_TRAINSETTYPE")
    private String trainsetType;

    @TableField("S_IMAGENAME")
    private String imageName;

    @TableField("S_STATE")
    private String state;

    public String getTrainsetType() {
        return trainsetType;
    }

    public void setTrainsetType(String trainsetType) {
        this.trainsetType = trainsetType;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "TrainsetImageDict{" +
                "trainsetType='" + trainsetType + '\'' +
                ", imageName='" + imageName + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
