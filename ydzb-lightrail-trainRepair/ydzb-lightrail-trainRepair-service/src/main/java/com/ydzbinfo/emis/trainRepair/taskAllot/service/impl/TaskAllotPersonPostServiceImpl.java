package com.ydzbinfo.emis.trainRepair.taskAllot.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.taskAllot.dao.TaskAllotPersonPostMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPersonPost;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.ITaskAllotPersonPostService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhangtk
 * @since 2021-03-29
 */
@Service
public class TaskAllotPersonPostServiceImpl extends ServiceImpl<TaskAllotPersonPostMapper, TaskAllotPersonPost> implements ITaskAllotPersonPostService {
    @Resource
    private TaskAllotPersonPostMapper taskAllotPersonPostMapper;
    @Override
    public void addTaskAllotPersonPost(TaskAllotPersonPost taskAllotPersonPost) {
        taskAllotPersonPostMapper.insert(taskAllotPersonPost);
    }
}
