package com.ydzbinfo.emis.trainRepair.taskAllot.dao;

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
public interface GroupModelMapper {
    List<GroupModel> getPerSonNelList(@Param("deptCode")String deptCode,@Param("workerId")String workerId);
    List<GroupModel> getGroupList(@Param("deptCode")String deptCode);

    List<PerSonNelModel> getPerSonNel(@Param("id")String id);
    List<PostModel> getPostListByStaffId(@Param("id")String id);
    List<PostModel> getPostByStaffId(@Param("id")String id);

    //冯帅
    List<PostModel> getPostList(@Param("deptCode") String deptCode,@Param("unitCode") String unitCode);

    //冯帅
    List<GroupModel> getBranchPersonList(GroupModel groupModel);
}
