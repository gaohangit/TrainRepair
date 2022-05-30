package com.ydzbinfo.emis.trainRepair.repairmanagent.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyBCritertionDict;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyBPowerDict;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyCWorkcritertion;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.Post;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author
 * @since 2020-07-21
 */
public interface IXzyCWorkcritertionService extends IService<XzyCWorkcritertion> {

    /**
     * @author: 冯帅
     * @Date: 2021/4/22 16:21
     * @Description:  获取作业标准配置列表数据
     */
    List<XzyCWorkcritertion> getWorkcritertionList(Page page,Map<String,String> paramMap);

    /**
     * 根据车组ID获取作业标准
     */
    List<XzyCWorkcritertion> getWorkcritertionLByTrainsetId(String trainsetId);

    /**
     * 根据车组ID获取作业标准
     */
    List<XzyCWorkcritertion> getWorkcritertionLByTrainsetIdOne(String trainsetId);


    //添加作业标准总方法
    boolean addModel(XzyCWorkcritertion xzyCWorkcritertion);

    //向kafka通道中推送新增数据
    boolean sendOneCreateData(XzyCWorkcritertion xzyCWorkcritertion);

    /**
     * @author: 冯帅
     * @Date: 2021/4/23 16:30
     * @Description:  添加作业标准主表
     */
    boolean addWorkcritertion(XzyCWorkcritertion xzyCWorkcritertion);

    List<Map<String, Object>> getWorkContent(Map<Object, Object> map);

    /**
     * @author: 冯帅
     * @Date: 2021/4/23 16:21
     * @Description: 获取预警角色下拉框
     */
    List<XzyBCritertionDict> getCritertionDict();

    /**
     * @author: 冯帅
     * @Date: 2021/4/23 16:21
     * @Description: 获取供断电下拉框
     */
    List<XzyBPowerDict> getPowerStateDict();

    /**
     * @author: 冯帅
     * @Date: 2021/4/23 16:21
     * @Description: 删除作业标准（假删除）
     */
    boolean delWorkcritertion(List<String> delWorkcritertionIds);

    //删除对象
    boolean delModel(List<XzyCWorkcritertion> delList);

    boolean sendOneDeleteData(List<XzyCWorkcritertion> xzyCWorkcritertionList);

    /**
     * @author: 冯帅
     * @Date: 2021/4/23 16:21
     * @Description: 获取岗位集合
     */
    List<Post> getPostList();
}
