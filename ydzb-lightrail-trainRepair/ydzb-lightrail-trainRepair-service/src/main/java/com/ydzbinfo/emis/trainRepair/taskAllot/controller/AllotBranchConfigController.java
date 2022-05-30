package com.ydzbinfo.emis.trainRepair.taskAllot.controller;

import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.trainRepair.common.model.User;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.GroupModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.PerSonNelModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.PostModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.common.TaskAllotUser;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.AllotBranchConfig;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.AllotPersonConfig;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.PersonPost;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IAllotBranchConfigService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IAllotPersonConfigService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IPersonPostService;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/15 16:31
 * @Modify 冯帅 2021-06-29
 **/
@RestController
@RequestMapping("/xzyCAllotbranchConfig")
public class AllotBranchConfigController {
    protected static final Logger logger = LoggerFactory.getLogger(AllotBranchConfigController.class);

    @Autowired
    private IAllotBranchConfigService allotBranchConfigService;
    @Autowired
    private IAllotPersonConfigService allotPersonConfigService;
    @Autowired
    private IPersonPostService personPostService;


    
    @ApiOperation("获取班组")
    @RequestMapping(value = "/getGroup")
    public Map getGroup(@RequestBody Map<String, String> map) {
        String deptCode = map.get("deptCode");
        Map<String, Object> result = new HashMap<>();
        try {
            if (StringUtils.isBlank(deptCode)) {
                User user = UserUtil.getUserInfo();
                if (user.getWorkTeam() != null) {
                    deptCode = user.getWorkTeam().getTeamCode();
                }
            }
            return allotBranchConfigService.getGroup(deptCode);
        } catch (Exception e) {
            logger.error("/xzyCAllotbranchConfig/getGroup报错："+e);
            result.put("deptUserList", null);
            result.put("data", null);
            result.put("msg", "查询失败");
            result.put("code", 0);
        }
        return result;
    }


    @ApiOperation("变更小组及人员")
    @PostMapping(value = "/setBranch")
    @Transactional(propagation = Propagation.REQUIRED)
    public RestResult setBranch(@RequestBody List<GroupModel> groupModels) {
        String unitCode = ContextUtils.getUnitCode();
        String deptCode = UserUtil.getUserInfo().getWorkTeam().getTeamCode();
        logger.info("/xzyCAllotbranchConfig/setBranch----开始调用变更小组及人员接口...");
        RestResult result = RestResult.success();
        try {
            //获取当前用户
            ShiroUser currentUser = ShiroKit.getUser();
            //获取当前时间
            Date currentDate = new Date();
            //删除小组
            MybatisPlusUtils.delete(
                allotBranchConfigService,
                eqParam(AllotBranchConfig::getDeptCode, deptCode),
                eqParam(AllotBranchConfig::getUnitCode, unitCode)
            );
            //删除人员
            MybatisPlusUtils.delete(
                allotPersonConfigService,
                eqParam(AllotPersonConfig::getDeptCode, deptCode),
                eqParam(AllotPersonConfig::getUnitCode, unitCode),
                eqParam(AllotPersonConfig::getFlag, "1")
            );
            //删除岗位配置
            MybatisPlusUtils.delete(
                personPostService,
                eqParam(PersonPost::getDeptCode, deptCode),
                eqParam(PersonPost::getUnitCode, unitCode)
            );

            //插入
            List<AllotBranchConfig> insertBranchConfigList = new ArrayList<>();//派工小组
            List<AllotPersonConfig> insertPersonConfigList = new ArrayList<>();//派工小组下人员
            List<PersonPost> insertPersonPostList = new ArrayList<>();//人员岗位配置
            for (GroupModel groupModel : groupModels) {
                //组织小组
                AllotBranchConfig insertBranchConfig = new AllotBranchConfig();
                BeanUtils.copyProperties(groupModel, insertBranchConfig);
                String branchCode = UUID.randomUUID().toString();
                insertBranchConfig.setBranchCode(branchCode);
                insertBranchConfig.setCreateTime(currentDate);
                insertBranchConfig.setCreateUserCode(UserUtil.getUserInfo().getStaffId());
                insertBranchConfig.setCreateUserName(ContextUtils.getUnitName());
                insertBranchConfig.setDeptCode(deptCode);
                insertBranchConfig.setUnitCode(unitCode);
                insertBranchConfig.setFlag("1");
                insertBranchConfigList.add(insertBranchConfig);
                //组织小组下人员
                for (PerSonNelModel perSonNelModel : groupModel.getPerSonNelModels()) {
                    AllotPersonConfig insertPersonConfig = new AllotPersonConfig();
                    String reePairPeRSonAllotId = UUID.randomUUID().toString();
                    BeanUtils.copyProperties(perSonNelModel, insertPersonConfig);
                    insertPersonConfig.setRepairPersonAllotId(reePairPeRSonAllotId);
                    insertPersonConfig.setDeptCode(deptCode);
                    insertPersonConfig.setBranchCode(branchCode);
                    insertPersonConfig.setBranchName(groupModel.getBranchName());
                    insertPersonConfig.setUnitCode(unitCode);
                    insertPersonConfig.setCreateUserCode(ContextUtils.getUnitCode());
                    insertPersonConfig.setCreateUserName(ContextUtils.getUnitName());
                    insertPersonConfig.setCreateTime(currentDate);
                    insertPersonConfig.setFlag("1");
                    insertPersonConfigList.add(insertPersonConfig);
                    //组织人员下岗位
                    List<PostModel> postModelList = perSonNelModel.getPostModelList();
                    if (postModelList != null && postModelList.size() > 0) {
                        for (PostModel postModel : postModelList) {
                            PersonPost insertPersonPost = new PersonPost();
                            String id = UUID.randomUUID().toString();
                            insertPersonPost.setId(id);
                            insertPersonPost.setPostId(postModel.getPostId());
                            insertPersonPost.setBranchCode(branchCode);
                            insertPersonPost.setStaffId(perSonNelModel.getWorkCode());
                            insertPersonPost.setStaffName(perSonNelModel.getWorkName());
                            insertPersonPost.setDeptCode(deptCode);
                            insertPersonPost.setUnitCode(unitCode);
                            insertPersonPost.setCreateUserCode(currentUser.getStaffId());
                            insertPersonPost.setCreateUserName(currentUser.getName());
                            insertPersonPost.setCreateTime(currentDate);
                            insertPersonPost.setFlag("1");
                            insertPersonPostList.add(insertPersonPost);
                        }
                    }
                }
            }
            //插入
            if (insertBranchConfigList.size() > 0) {
                allotBranchConfigService.insertBatch(insertBranchConfigList);
            }
            if (insertPersonConfigList.size() > 0) {
                allotPersonConfigService.insertBatch(insertPersonConfigList);
            }
            if (insertPersonPostList.size() > 0) {
                personPostService.insertBatch(insertPersonPostList);
            }
            result.setMsg("变更小组及人员成功");
        } catch (Exception ex) {
            logger.error("/xzyCAllotbranchConfig/setBranch----调用变更小组及人员接口出错...", ex);
            result = RestResult.fromException(ex, logger, "变更小组及人员失败");
        }
        logger.info("/xzyCAllotbranchConfig/setBranch----调用变更小组及人员接口结束...");
        return result;
    }

    @ApiOperation("非小组人员配置岗位")
    @PostMapping(value = "/setPostBySon")
    @Transactional
    public Object setPostBySon(@RequestBody List<TaskAllotUser> taskAllotUsers) {
        try {
            User user = UserUtil.getUserInfo();
            int sort = 0;
            for (TaskAllotUser taskAllotUser : taskAllotUsers) {
                AllotPersonConfig allotPersonConfig = new AllotPersonConfig();
                allotPersonConfig.setRepairPersonAllotId(taskAllotUser.getId());
                allotPersonConfig.setSort(String.valueOf(sort));
                allotPersonConfigService.updAllotPersonConfig(allotPersonConfig);
                if (taskAllotUser.getPostModelList() != null) {
                    for (PostModel postModel : taskAllotUser.getPostModelList()) {
                        PersonPost personPost = new PersonPost();
                        personPost.setPostId(postModel.getPostId());
                        personPost.setDeptCode(user.getWorkTeam().getTeamCode());
                        personPost.setStaffId(taskAllotUser.getStaffId());
                        personPost.setFlag("1");
                        personPost.setUnitCode(ContextUtils.getUnitCode());
                        personPostService.insert(personPost);
                    }
                }
                sort++;
            }
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "非小组人员配置岗位失败");
        }
    }
}
