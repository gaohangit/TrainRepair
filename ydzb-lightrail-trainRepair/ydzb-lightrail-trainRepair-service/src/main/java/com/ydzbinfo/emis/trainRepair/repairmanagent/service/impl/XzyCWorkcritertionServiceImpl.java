package com.ydzbinfo.emis.trainRepair.repairmanagent.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jxdinfo.hussar.core.mq.MessageUtil;
import com.jxdinfo.hussar.core.mq.MessageWrapper;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.configs.kafka.workcriterion.WorkCriterionMqSource;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.*;
import com.ydzbinfo.emis.trainRepair.repairmanagent.constant.WorkCritertionHeaderConstant;
import com.ydzbinfo.emis.trainRepair.repairmanagent.dao.XzyCWorkcritertionMapper;
import com.ydzbinfo.emis.trainRepair.repairmanagent.service.IWorkcritertionPostService;
import com.ydzbinfo.emis.trainRepair.repairmanagent.service.IWorkcritertionPriRoleService;
import com.ydzbinfo.emis.trainRepair.repairmanagent.service.IXzyCWorkcritertionRoleService;
import com.ydzbinfo.emis.trainRepair.repairmanagent.service.IXzyCWorkcritertionService;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.Post;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 * @since 2020-07-21
 */
@Service
public class XzyCWorkcritertionServiceImpl extends ServiceImpl<XzyCWorkcritertionMapper, XzyCWorkcritertion> implements IXzyCWorkcritertionService {

    protected static final Logger logger = LoggerFactory.getLogger(XzyCWorkcritertionServiceImpl.class);

    //预警角色服务
    @Autowired
    IXzyCWorkcritertionRoleService xzyCWorkcritertionRoleService;

    //岗位角色服务
    @Autowired
    IWorkcritertionPostService workcritertionPostService;

    //优先角色服务
    @Autowired
    IWorkcritertionPriRoleService workcritertionPriRoleService;

    @Autowired
    XzyCWorkcritertionMapper xzyCWorkcritertionMapper;

    //调用其它接口服务
    @Resource
    IRemoteService iRemoteService;

    @Autowired(required = false)
    private WorkCriterionMqSource workCriterionSource;

    /**
     * @author: 冯帅
     * @Date: 2021/4/22 16:21
     * @Description: 获取作业标准配置列表数据
     */
    @Override
    public List<XzyCWorkcritertion> getWorkcritertionList(Page page, Map<String, String> paramMap) {
        if (paramMap.containsKey("itemName")) {
            paramMap.put("itemName", MybatisOgnlUtils.replaceWildcardChars(paramMap.get("itemName")));
        }
        List<XzyCWorkcritertion> xzyCWorkcritertionList = xzyCWorkcritertionMapper.getWorkcritertionList(page, paramMap);
        List<String> sCritertionidList = xzyCWorkcritertionList.stream().map(t -> t.getsCritertionid()).collect(Collectors.toList());
        String sCritertionids = "('" + String.join("','", sCritertionidList) + "')";
        List<XzyCWorkcritertionRole> xzyCWorkcritertionRoleList = xzyCWorkcritertionMapper.getWorkcritertionRoleList(sCritertionids);
        List<XzyCWorkcritertionPost> xzyCWorkcritertionPostList = xzyCWorkcritertionMapper.getWorkcritertionPostList(sCritertionids);
        List<XzyCWorkritertionPriRole> xzyCWorkritertionPriRoleList = xzyCWorkcritertionMapper.getWorkcritertionPriRoleList(sCritertionids);

        for (XzyCWorkcritertion item : xzyCWorkcritertionList) {
            String critertionid = item.getsCritertionid();
            List<XzyCWorkcritertionRole> itemRoleList = xzyCWorkcritertionRoleList.stream().filter(t -> t.getsCritertionid().equals(critertionid)).collect(Collectors.toList());
            List<XzyCWorkcritertionPost> itemPostList = xzyCWorkcritertionPostList.stream().filter(t -> t.getSCritertionid().equals(critertionid)).collect(Collectors.toList());
            List<XzyCWorkritertionPriRole> itemPriRoleList = xzyCWorkritertionPriRoleList.stream().filter(t -> t.getSCritertionid().equals(critertionid)).collect(Collectors.toList());
            item.setXzyCWorkcritertionRoleList(itemRoleList);
            item.setXzyCWorkcritertionPostList(itemPostList);
            item.setXzyCWorkritertionPriRoleList(itemPriRoleList);
        }

        return xzyCWorkcritertionList;
    }



    /**
     * 根据车组ID获取作业标准
     */
    public List<XzyCWorkcritertion> getWorkcritertionLByTrainsetId(String trainsetId) {
        List<XzyCWorkcritertion> xzyCWorkcritertionList = new ArrayList<>();
        //调用履历接口获取车组基本信息
        TrainsetBaseInfo trainsetBaseInfo = RemoteServiceCachedDataUtil.getTrainsetBaseinfoByID(trainsetId);
        //根据车型和批次获取作业标准
        //查询作业标准配置表
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("trainsetType", trainsetBaseInfo.getTraintype());
        paramMap.put("trainsetSubType", trainsetBaseInfo.getTraintempid());
        Page page = new Page(1, Integer.MAX_VALUE);
        xzyCWorkcritertionList = this.getWorkcritertionList(page, paramMap);
        return xzyCWorkcritertionList;
    }

    /**
     * 根据车组ID获取作业标准
     */
    public List<XzyCWorkcritertion> getWorkcritertionLByTrainsetIdOne(String trainsetId) {
        List<XzyCWorkcritertion> xzyCWorkcritertionList = new ArrayList<>();
        //调用履历接口获取车组基本信息
        TrainsetBaseInfo trainsetBaseInfo = RemoteServiceCachedDataUtil.getTrainsetBaseinfoByID(trainsetId);
        //根据车型和批次获取作业标准
        //查询作业标准配置表
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("trainsetType", trainsetBaseInfo.getTraintype());
        paramMap.put("trainsetSubType", trainsetBaseInfo.getTraintempid());
        paramMap.put("cyc","1");
        Page page = new Page(1, Integer.MAX_VALUE);
        xzyCWorkcritertionList = this.getWorkcritertionList(page, paramMap);
        return xzyCWorkcritertionList;
    }


    /**
     * @author: 冯帅
     * @Date: 2021/4/23 16:30
     * @Description: 添加作业标准主表
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean addWorkcritertion(XzyCWorkcritertion xzyCWorkcritertion) {
        //获取当前用户
        ShiroUser currentUser = ShiroKit.getUser();
        if (!ObjectUtils.isEmpty(xzyCWorkcritertion)) {
            //1.给主表对象赋值
            xzyCWorkcritertion.setsCritertionid(UUID.randomUUID().toString());
            xzyCWorkcritertion.setcFlag("1");
            xzyCWorkcritertion.setCreateTime(new Date());
            xzyCWorkcritertion.setCreateUserCode(currentUser.getStaffId());
            xzyCWorkcritertion.setCreateUserName(currentUser.getName());
            if(StringUtils.isNotBlank(xzyCWorkcritertion.getsItemcode())){
                xzyCWorkcritertion.setsItemcode(xzyCWorkcritertion.getsItemcode());
            }else{
            xzyCWorkcritertion.setsItemcode(UUID.randomUUID().toString());
            }

            //2.给预警角色对象赋值
            List<XzyCWorkcritertionRole> roleList = xzyCWorkcritertion.getXzyCWorkcritertionRoleList();
            if (!CollectionUtils.isEmpty(roleList)) {
                roleList.stream().map(roleItem -> {
                    roleItem.setsCritertionid(xzyCWorkcritertion.getsCritertionid());
                    roleItem.setsId(UUID.randomUUID().toString());
                    roleItem.setcFlag("1");
                    return roleItem;
                }).collect(Collectors.toList());
                xzyCWorkcritertion.setXzyCWorkcritertionRoleList(roleList);
            }
            //3.给预警优先角色赋值
            List<XzyCWorkritertionPriRole> priRoleList = xzyCWorkcritertion.getXzyCWorkritertionPriRoleList();
            if (!CollectionUtils.isEmpty(priRoleList)) {
                priRoleList.stream().map(priRoleItem -> {
                    priRoleItem.setSCritertionid(xzyCWorkcritertion.getsCritertionid());
                    priRoleItem.setId(UUID.randomUUID().toString());
                    priRoleItem.setFlag("1");
                    return priRoleItem;
                }).collect(Collectors.toList());
                xzyCWorkcritertion.setXzyCWorkritertionPriRoleList(priRoleList);
            }
            //4.给岗位角色赋值
            List<XzyCWorkcritertionPost> postList = xzyCWorkcritertion.getXzyCWorkcritertionPostList();
            if (!CollectionUtils.isEmpty(postList)) {
                postList.stream().map(postItem -> {
                    postItem.setSCritertionid(xzyCWorkcritertion.getsCritertionid());
                    postItem.setId(UUID.randomUUID().toString());
                    postItem.setFlag("1");
                    return postItem;
                }).collect(Collectors.toList());
                xzyCWorkcritertion.setXzyCWorkcritertionPostList(postList);
            }
        }
        boolean insertFlag = this.addModel(xzyCWorkcritertion);
        //向kafka中推送数据
        if (SpringCloudStreamUtil.enableSendCloudData(WorkCriterionMqSource.class)) {
            this.sendOneCreateData(xzyCWorkcritertion);
            System.out.println("推送新增数据");
        }
        return insertFlag;
    }


    /**
     * @author: 冯帅
     * @Date: 2021/4/23 16:30
     * @Description: 添加作业标准总方法
     */
    @Override
    @Transactional
    public boolean addModel(XzyCWorkcritertion xzyCWorkcritertion) {
        //1.获取预警角色、优先预警角色、岗位角色集合
        List<XzyCWorkcritertionRole> roleList = xzyCWorkcritertion.getXzyCWorkcritertionRoleList();
        List<XzyCWorkcritertionPost> postList = xzyCWorkcritertion.getXzyCWorkcritertionPostList();
        List<XzyCWorkritertionPriRole> priRoleList = xzyCWorkcritertion.getXzyCWorkritertionPriRoleList();
        //2.添加主表
        xzyCWorkcritertionMapper.insert(xzyCWorkcritertion);
        //3.添加作业标准-预警角色
        if (!CollectionUtils.isEmpty(roleList)) {
            xzyCWorkcritertionRoleService.insertBatch(roleList);
        }
        //4.添加作业标准-岗位
        if (!CollectionUtils.isEmpty(postList)) {
            workcritertionPostService.insertBatch(postList);
        }
        //5.添加作业标准-优先角色
        if (!CollectionUtils.isEmpty(priRoleList)) {
            workcritertionPriRoleService.insertBatch(priRoleList);
        }
        return true;
    }

    @Override
    public boolean sendOneCreateData(XzyCWorkcritertion xzyCWorkcritertion) {
        boolean sendFlag = false;
        try {
            //将数据集合推送到kafka中
            MessageWrapper<XzyCWorkcritertion> messageWrapper = new MessageWrapper<>(WorkCritertionHeaderConstant.class, WorkCritertionHeaderConstant.CREATE_ONE, xzyCWorkcritertion);
            sendFlag = MessageUtil.sendMessage(workCriterionSource.workcritertiononeChannel(), messageWrapper);
            if (sendFlag) {
                logger.info("一级修作业标准配置往kafka推送数据成功");
            } else {
                logger.info("一级修作业标准配置往kafka推送数据失败");
            }
        } catch (Exception ex) {
            sendFlag = false;
            logger.error("一级修作业标准配置往kafka推送数据出错" + ex);
        }
        return sendFlag;
    }

    @Override
    public List<Map<String, Object>> getWorkContent(Map<Object, Object> map) {
        return xzyCWorkcritertionMapper.getWorkContent(map);
    }

    @Override
    public List<XzyBCritertionDict> getCritertionDict() {
        return xzyCWorkcritertionMapper.getCritertionDict();
    }

    @Override
    public List<XzyBPowerDict> getPowerStateDict() {
        return xzyCWorkcritertionMapper.getPowerStateDict();
    }

    /**
     * @author: 冯帅
     * @Date: 2021/4/23 16:21
     * @Description: 删除作业标准（假删除）
     */
    @Override
    @Transactional
    public boolean delWorkcritertion(List<String> delWorkcritertionIds) {
        if (!CollectionUtils.isEmpty(delWorkcritertionIds)) {
            //1.获取当前用户
            ShiroUser currentUser = ShiroKit.getUser();
            String userCode = currentUser.getStaffId();
            String userName = currentUser.getName();
            //2.获取当前时间
            Date currentDate = new Date();
            List<XzyCWorkcritertion> delList = new ArrayList<>();
            //3.循环进行删除
            delWorkcritertionIds.forEach(delId -> {
                //4.组织删除对象
                XzyCWorkcritertion delModel = new XzyCWorkcritertion();
                delModel.setsCritertionid(delId);
                delModel.setcFlag("0");
                delModel.setDelUserCode(userCode);
                delModel.setDelUserName(userName);
                delModel.setDelTime(currentDate);
                delList.add(delModel);
            });
            //5.调用删除方法进行删除
            boolean delFlag = this.delModel(delList);
            //6.往kafka通道中推送数据
            if (delFlag && SpringCloudStreamUtil.enableSendCloudData(WorkCriterionMqSource.class)) {
                this.sendOneDeleteData(delList);
                System.out.println("推送删除数据");
            }
        }
        return true;
    }

    @Override
    @Transactional
    public boolean delModel(List<XzyCWorkcritertion> delList) {
        if (!CollectionUtils.isEmpty(delList)) {
            delList.forEach(delItem -> {
                //1.删除主表
                XzyCWorkcritertion delCritertion = new XzyCWorkcritertion();
                delCritertion.setcFlag("0");
                delCritertion.setDelUserCode(delItem.getDelUserCode());
                delCritertion.setDelUserName(delItem.getDelUserName());
                delCritertion.setDelTime(delItem.getDelTime());

                MybatisPlusUtils.update(
                    xzyCWorkcritertionMapper,
                    delCritertion,
                    eqParam(XzyCWorkcritertion::getsCritertionid, delItem.getsCritertionid())
                );
                //2.删除预警角色表
                XzyCWorkcritertionRole delRole = new XzyCWorkcritertionRole();
                delRole.setcFlag("0");
                MybatisPlusUtils.update(
                    xzyCWorkcritertionRoleService,
                    delRole,
                    eqParam(XzyCWorkcritertionRole::getsCritertionid, delItem.getsCritertionid())
                );
                //3.删除岗位角色表
                XzyCWorkcritertionPost delPost = new XzyCWorkcritertionPost();
                delPost.setFlag("0");
                MybatisPlusUtils.update(
                    workcritertionPostService,
                    delPost,
                    eqParam(XzyCWorkcritertionPost::getSCritertionid, delItem.getsCritertionid())
                );

                //4.删除优先角色表
                XzyCWorkritertionPriRole delPriRole = new XzyCWorkritertionPriRole();
                delPriRole.setFlag("0");
                MybatisPlusUtils.update(
                    workcritertionPriRoleService,
                    delPriRole,
                    eqParam(XzyCWorkritertionPriRole::getSCritertionid, delItem.getsCritertionid())
                );
            });
        }
        return true;
    }

    @Override
    public boolean sendOneDeleteData(List<XzyCWorkcritertion> xzyCWorkcritertionList) {
        boolean sendFlag = false;
        try {
            //将数据集合推送到kafka中
            MessageWrapper<List<XzyCWorkcritertion>> messageWrapper = new MessageWrapper<>(WorkCritertionHeaderConstant.class, WorkCritertionHeaderConstant.DELETE_ONE, xzyCWorkcritertionList);
            sendFlag = MessageUtil.sendMessage(workCriterionSource.workcritertiononeChannel(), messageWrapper);
            if (sendFlag) {
                logger.info("一级修作业标准配置往kafka推送删除数据成功");
            } else {
                logger.info("一级修作业标准配置往kafka推送删除数据失败");
            }
        } catch (Exception ex) {
            sendFlag = false;
            logger.error("一级修作业标准配置往kafka推送删除数据出错" + ex);
        }
        return sendFlag;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/4/23 16:21
     * @Description: 获取岗位集合
     */
    @Override
    public List<Post> getPostList() {
        return xzyCWorkcritertionMapper.getPostList();
    }


}
