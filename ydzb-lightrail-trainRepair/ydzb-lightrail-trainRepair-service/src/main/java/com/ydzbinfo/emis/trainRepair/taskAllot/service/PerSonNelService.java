package com.ydzbinfo.emis.trainRepair.taskAllot.service;

import com.ydzbinfo.emis.trainRepair.taskAllot.model.GroupModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.PerSonNelModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.PostModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/22 17:56
 **/
public interface PerSonNelService {
    List<GroupModel> getPerSonNelList(@Param("deptCode")String deptCode,@Param("workerId")String workerId);
    List<GroupModel> getGroupList(@Param("deptCode")String deptCode);
    List<PerSonNelModel> getPerSonNel(@Param("id")String id);

    List<PostModel> getPostListByStaffId(@Param("id")String id);
    List<PostModel> getPostByStaffId(@Param("id")String id);

    //冯帅
    List<PostModel> getPostList(String deptCode,String unitCode);

    //冯帅
    List<GroupModel> getBranchPersonList(GroupModel groupModel);
}
