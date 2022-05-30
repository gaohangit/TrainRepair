package com.ydzbinfo.emis.trainRepair.mobile.model;

import lombok.Data;

import java.util.List;

/**
 * Description:
 * Author: 吴跃常
 * Create Date Time: 2021/8/3 10:57
 * Update Date Time: 2021/8/3 10:57
 *
 * @see
 */
@Data
public class HomeAllotTask {

    private String homeAllotTask;

    private List<MobileLoginTaskEntity> mobileLoginTaskEntities;
}
