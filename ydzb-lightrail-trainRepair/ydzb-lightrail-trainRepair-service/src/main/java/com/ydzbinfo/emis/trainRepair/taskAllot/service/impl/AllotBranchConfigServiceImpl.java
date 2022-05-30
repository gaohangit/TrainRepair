package com.ydzbinfo.emis.trainRepair.taskAllot.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jxdinfo.hussar.common.userutil.UserUtil;
import com.ydzbinfo.emis.trainRepair.common.model.User;
import com.ydzbinfo.emis.trainRepair.taskAllot.controller.AllotBranchConfigController;
import com.ydzbinfo.emis.trainRepair.taskAllot.dao.AllotbranchConfigMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.GroupModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.PerSonNelModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.PostModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.common.TaskAllotUser;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.AllotBranchConfig;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.AllotPersonConfig;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IAllotBranchConfigService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IAllotPersonConfigService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.PerSonNelService;
import com.ydzbinfo.emis.utils.ContextUtils;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.hussar.system.bsp.organ.SysStaff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhangtk
 * @since 2021-03-23
 */
@Service
public class AllotBranchConfigServiceImpl extends ServiceImpl<AllotbranchConfigMapper, AllotBranchConfig> implements IAllotBranchConfigService {

    protected static final Logger logger = LoggerFactory.getLogger(AllotBranchConfigServiceImpl.class);


    @Autowired
    private PerSonNelService perSonNelService;
    @Autowired
    IAllotPersonConfigService allotPersonConfigService;

    @Override
    public Map getGroup(String deptCode) {
        logger.info("/xzyCAllotbranchConfig/getGroup开始获取当前用户信息");
        User user = com.ydzbinfo.emis.utils.UserUtil.getUserInfo();
        logger.info("/xzyCAllotbranchConfig/getGroup获取当前用户信息结束");
        //1.根据班组编码获取班组下所有人员集合（轻骑兵平台）
        logger.info("/xzyCAllotbranchConfig/getGroup开始根据班组编码获取班组下所有人员信息");
        List<SysStaff> staffList = UserUtil.getStaffListByDeptIncludePartTimeDept(deptCode);
        logger.info("/xzyCAllotbranchConfig/getGroup根据班组编码获取班组下所有人员信息结束");
        List<TaskAllotUser> taskAllotUserList = new ArrayList<>();
        if (staffList != null && staffList.size() > 0) {
            for (SysStaff sysUser : staffList) {
                TaskAllotUser taskAllotUser = new TaskAllotUser();
                taskAllotUser.setStaffId(sysUser.getStaffId());
                taskAllotUser.setName(sysUser.getName());
                taskAllotUserList.add(taskAllotUser);
            }
        }

        //2.根据班组id获取人员岗位关系集合
        List<PostModel> postPersonList = perSonNelService.getPostList(deptCode, "");
        //3.给非兼职、非小组 成员挂岗位
        if (taskAllotUserList != null && taskAllotUserList.size() > 0 && postPersonList != null && postPersonList.size() > 0) {
            taskAllotUserList = taskAllotUserList.stream().map(userItem -> {
                //根据人员id查询出来相关岗位
                List<PostModel> posts = postPersonList.stream().filter(t -> t.getStaffId().equals(userItem.getStaffId()) && StringUtils.isBlank(t.getBranchCode())).collect(Collectors.toList());
                if (posts != null && posts.size() > 0) {
                    userItem.setPostModelList(posts);
                }
                return userItem;
            }).collect(Collectors.toList());
        }
        //4.根据班组编码获取该班组下边的小组 及小组下边的人员 集合
        GroupModel groupSelModel = new GroupModel();
        groupSelModel.setDeptCode(deptCode);
        List<GroupModel> branchPersonList = perSonNelService.getBranchPersonList(groupSelModel);

        //5.给有小组的人员挂岗位
        if (branchPersonList != null && branchPersonList.size() > 0) {
            for (GroupModel groupItem : branchPersonList) {
                List<PerSonNelModel> personList = groupItem.getPerSonNelModels();
                if (personList != null && personList.size() > 0) {
                    personList = personList.stream().map(personItem -> {
                        List<PostModel> posts = postPersonList.stream().filter(
                            t -> t.getDeptCode().equals(personItem.getDeptCode())
                                && t.getStaffId().equals(personItem.getWorkCode())
                                && String.valueOf(t.getBranchCode()).equals(personItem.getBranchCode())
                                && StringUtils.isNotBlank(personItem.getBranchCode())).collect(Collectors.toList());
                        personItem.setPostModelList(posts);
                        return personItem;
                    }).collect(Collectors.toList());
                }
            }
        }
        //过滤掉已经在组内的人员并且不是兼职的人员
        List<String> staffIds = new ArrayList<>();
        for (GroupModel groupModel : branchPersonList) {
            staffIds.addAll(groupModel.getPerSonNelModels().stream().filter(n -> !n.getPartTime().equals("1")).map(v -> v.getWorkCode()).collect(Collectors.toList()));
        }
        taskAllotUserList = taskAllotUserList.stream().filter(v -> !staffIds.contains(v.getStaffId())).collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();

        //库里不在组内的人员
        List<AllotPersonConfig> allotPersonConfigs = MybatisPlusUtils.selectList(
            allotPersonConfigService,
            eqParam(AllotPersonConfig::getDeptCode, deptCode),
            eqParam(AllotPersonConfig::getUnitCode, ContextUtils.getUnitCode()),
            eqParam(AllotPersonConfig::getFlag, "3")
        );
        allotPersonConfigs.sort(Comparator.comparing(AllotPersonConfig::getSort));
        List<String> staffId = allotPersonConfigs.stream().map(v -> v.getWorkCode()).collect(Collectors.toList());
        List<TaskAllotUser> addTaskAllotUsers = taskAllotUserList.stream().filter(v -> !staffId.contains(v.getStaffId())).collect(Collectors.toList());
        int sort = 0;
        for (TaskAllotUser taskAllotUser : addTaskAllotUsers) {
            AllotPersonConfig allotPersonConfig = new AllotPersonConfig();
            allotPersonConfig.setDeptCode(deptCode);
            allotPersonConfig.setWorkCode(taskAllotUser.getStaffId());
            allotPersonConfig.setWorkName(taskAllotUser.getName());
            allotPersonConfig.setUnitCode(ContextUtils.getUnitCode());
            allotPersonConfig.setSort(String.valueOf(sort));
            allotPersonConfig.setFlag("3");
            allotPersonConfig.setRepairPersonAllotId(UUID.randomUUID().toString());
            allotPersonConfigService.insert(allotPersonConfig);
            taskAllotUser.setId(allotPersonConfig.getRepairPersonAllotId());
            allotPersonConfigs.add(allotPersonConfig);
            sort++;
        }
        taskAllotUserList.sort((a, b) -> {
            int sortA = Integer.parseInt(allotPersonConfigs.stream().filter(v -> v.getWorkCode().equals(a.getStaffId())).collect(Collectors.toList()).get(0).getSort());
            int sortB = Integer.parseInt(allotPersonConfigs.stream().filter(v -> v.getWorkCode().equals(b.getStaffId())).collect(Collectors.toList()).get(0).getSort());
            if (sortA > sortB) {
                return 1;
            } else if (sortA < sortB) {
                return -1;
            } else {
                return 0;
            }
        });

        for (TaskAllotUser taskAllotUser : taskAllotUserList) {
            taskAllotUser.setId(allotPersonConfigs.stream().filter(v -> v.getWorkCode().equals(taskAllotUser.getStaffId())).collect(Collectors.toList()).get(0).getRepairPersonAllotId());
        }
        result.put("deptUserList", taskAllotUserList);
        result.put("data", branchPersonList);
        result.put("msg", "查询成功");
        result.put("code", 1);
        return result;
    }
}
