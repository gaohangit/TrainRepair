package com.ydzbinfo.emis.trainRepair.taskAllot.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPersonPost;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @since 2021-03-29
 */
public interface ITaskAllotPersonPostService extends IService<TaskAllotPersonPost> {
    void addTaskAllotPersonPost(TaskAllotPersonPost taskAllotPersonPost);

}
