package com.ydzbinfo.emis.trainRepair.taskAllot.service.impl;

import com.ydzbinfo.emis.trainRepair.taskAllot.dao.GroupModelMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.GroupModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.PerSonNelModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.PostModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.PerSonNelService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/22 18:03
 **/
@Service
public class PerSonNelServiceImpl implements PerSonNelService {
    @Resource
    private GroupModelMapper groupModelMapper;
    @Override
    public List<GroupModel> getPerSonNelList(String deptCode,String workerId) {
        return groupModelMapper.getPerSonNelList(deptCode,workerId);
    }

    @Override
    public List<GroupModel> getGroupList(String deptCode) {
        return groupModelMapper.getGroupList(deptCode);
    }

    @Override
    public  List<PerSonNelModel> getPerSonNel (String id) {
        return groupModelMapper.getPerSonNel(id);
    }

    @Override
    public List<PostModel> getPostListByStaffId(String id) {
        return groupModelMapper.getPostListByStaffId(id);
    }

    @Override
    public List<PostModel> getPostByStaffId(String id) {
        return groupModelMapper.getPostByStaffId(id);
    }


    //冯帅
    @Override
    public List<PostModel> getPostList(String deptCode,String unitCode){
        return groupModelMapper.getPostList(deptCode,unitCode);
    }

    //冯帅
    @Override
    public List<GroupModel> getBranchPersonList(GroupModel groupModel){
        return groupModelMapper.getBranchPersonList(groupModel);
    }
}
