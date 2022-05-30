package com.ydzbinfo.emis.trainRepair.repairmanagent.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.*;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.Post;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2020-07-21
 */
public interface XzyCWorkcritertionMapper extends BaseMapper<XzyCWorkcritertion> {

    /**
     * @author: 冯帅
     * @Date: 2021/4/22 16:21
     * @Description:  获取作业标准配置列表数据
     */
    List<XzyCWorkcritertion> getWorkcritertionList(Page page, Map<String,String> paramMap);

    List<XzyCWorkcritertion> getWorkcritertionListDelete(Map<String,String> paramMap);


    /**
     * @author: 冯帅
     * @Date: 2021/4/26 15:50
     * @Description: 根据sCritertionids获取预警角色关系集合
     */
    List<XzyCWorkcritertionRole> getWorkcritertionRoleList(@Param("sCritertionids") String sCritertionids);

    /**
     * @author: 冯帅
     * @Date: 2021/4/26 15:51
     * @Description: 根据sCritertionids获取岗位关系集合
     */
    List<XzyCWorkcritertionPost> getWorkcritertionPostList(@Param("sCritertionids") String sCritertionids);

    List<XzyCWorkcritertionPost> getWorkcritertionPostList1(@Param("sCritertionids") String sCritertionids);

    /**
     * @author: 冯帅
     * @Date: 2021/4/26 15:51
     * @Description: 根据sCritertionids获取优先角色关系集合
     */
    List<XzyCWorkritertionPriRole> getWorkcritertionPriRoleList(@Param("sCritertionids") String sCritertionids);

    /**
     * @author: 冯帅
     * @Date: 2021/4/23 16:21
     * @Description: 删除作业标准（假删除）
     */
    Integer delWorkcritertion(Map<String,String> paramMap);

    List<Map<String, Object>> getWorkContent(Map<Object, Object> map);

    List<XzyBCritertionDict> getCritertionDict();

    List<XzyBPowerDict> getPowerStateDict();

    /**
     * @author: 冯帅
     * @Date: 2021/4/23 16:21
     * @Description: 获取岗位集合
     */
    List<Post> getPostList();
}
