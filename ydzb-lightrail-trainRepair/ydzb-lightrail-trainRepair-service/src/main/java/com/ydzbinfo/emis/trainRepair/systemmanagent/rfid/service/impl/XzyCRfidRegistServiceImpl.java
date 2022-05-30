package com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.controller.RfidRegistController;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.dao.XzyCRfidRegistMapper;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.model.RfidCritertion;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.model.RfidPosition;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.model.XzyCRfidRegist;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.querymodel.XzyCRfidcarCritertion;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.querymodel.XzyCRfidcarPoistion;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.service.IXzyCRfidRegistService;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.service.XzyCRfidcarCritertionService;
import com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.service.XzyCRfidcarPoistionService;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParamUtil;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.emis.utils.mybatisplus.param.OrderBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author yyx
 * @since 2020-04-29
 */
@Service
public class XzyCRfidRegistServiceImpl extends ServiceImpl<XzyCRfidRegistMapper, XzyCRfidRegist> implements IXzyCRfidRegistService {

    @Autowired
    XzyCRfidRegistMapper xzyCRfidRegistMapper;

    /**
     * rfid标准关系业务类
     */
    @Autowired
    private XzyCRfidcarCritertionService xzyCRfidcarCritertionService;

    /**
     * rfid位置业务类
     */
    @Autowired
    private XzyCRfidcarPoistionService xzyCRfidcarPoistionService;

    protected static final Logger logger = LoggerFactory.getLogger(RfidRegistController.class);

    /**
     * RFID查询
     **/

    @Override
    public List<XzyCRfidRegist> QueryXzyCRfidregist(Map<Object, Object> map) {
        return xzyCRfidRegistMapper.QueryXzyCRfidregist(map);
    }

    @Override
    public List<XzyCRfidRegist> QueryXzyCRfidregistByTID(Map<Object, Object> map) {
        return xzyCRfidRegistMapper.QueryXzyCRfidregistByTID(map);
    }

    /**
     * RFID查询 页数量
     **/
    @Override
    public String QueryXzyCRfidregistCount(Map<Object, Object> map) {
        return xzyCRfidRegistMapper.QueryXzyCRfidregistCount(map);
    }

    /**
     * RFID注册成功查询
     **/
    @Override
    public List<XzyCRfidRegist> QueryXzyCRfidregistOK(Map<Object, Object> map) {
        return xzyCRfidRegistMapper.QueryXzyCRfidregistOK(map);
    }

    /**
     * RFID标签注册 判断TID是否注册
     **/
    @Override
    public String isHaveTID(Map<Object, Object> map) {
        return xzyCRfidRegistMapper.isHaveTID(map);
    }

    /**
     * 批量删除RFID标签（假删除）
     **/
    @Override
    public Integer delRfid(List<String> delIdList, String delUserCode, String delUserName) {
        MybatisPlusUtils.delete(
            xzyCRfidcarPoistionService,
            inParam(XzyCRfidcarPoistion::getTId, delIdList)
        );
        return xzyCRfidRegistMapper.delRfid(delIdList, delUserCode, delUserName);
    }

    @Override
    public RestResult selectRfIdList(XzyCRfidRegist model, Integer pageNum, Integer pageSize) {
        logger.info("rfidRegist/list------开始调用标签注册获取列表接口...");
        // 把需要like的属性分割出来
        model.setcFlag("1");
        ColumnParam<XzyCRfidRegist>[] eqColumnParams = MybatisPlusUtils.getEqColumnParamsFromEntity(
            model,
            XzyCRfidRegist.class,
            Arrays.asList(
                XzyCRfidRegist::getsTid,
                XzyCRfidRegist::getsRepairplacename,
                XzyCRfidRegist::getsTrackname,
                XzyCRfidRegist::getsUnitname
            )
        );
        List<ColumnParam<XzyCRfidRegist>> columnParamList = new ArrayList<>();
        columnParamList.addAll(Arrays.asList(eqColumnParams));
        columnParamList.addAll(ColumnParamUtil.filterBlankParamList(
            likeIgnoreCaseParam(XzyCRfidRegist::getsTid, model.getsTid()),
            likeIgnoreCaseParam(XzyCRfidRegist::getsRepairplacename, model.getsRepairplacename()),
            likeIgnoreCaseParam(XzyCRfidRegist::getsTrackname, model.getsTrackname()),
            likeIgnoreCaseParam(XzyCRfidRegist::getsUnitname, model.getsUnitname())
        ));

        List<OrderBy<XzyCRfidRegist>> orderByList = new ArrayList<>(Arrays.asList(
            orderBy(XzyCRfidRegist::getsTrackname, false),
            orderBy(XzyCRfidRegist::getsRepairplacename, false),
            orderBy(XzyCRfidRegist::getsPillarname, false)
        ));
        Page<XzyCRfidRegist> page = MybatisPlusUtils.selectPage(
            this,
            pageNum,
            pageSize,
            columnParamList,
            orderByList
        );
        logger.info("rfidRegist/list------调用标签注册获取列表接口结束...");
        return RestResult.success().setData(page);
    }

    @Override
    public RestResult selectRfIdPosition(Integer pageNum, Integer pageSize, String tid, String trackCode, String placeCode, Integer carCount, String unitCode) {
        Page<RfidPosition> rfidPositionPage = xzyCRfidcarPoistionService.selectRfIdPosition(pageNum, pageSize, tid, trackCode, placeCode, carCount, unitCode);
        return RestResult.success().setData(rfidPositionPage);
    }

    @Override
    public RestResult selectRfIdCriterion(Integer pageNum, Integer pageSize, String itemName, String trainsetSubType, String trainsetType, String repairPlaceCode) {
        Page<RfidCritertion> page = xzyCRfidcarCritertionService.selectRfIdCriterion(pageNum, pageSize, itemName, trainsetSubType, trainsetType, repairPlaceCode);
        return RestResult.success().setData(page);
    }

    @Override
    public void addPosition(RfidPosition rfidPosition, ShiroUser currentUser) {
        //判断位置是否已注册
        int count = MybatisPlusUtils.selectCount(
            xzyCRfidcarPoistionService,
            inParam(XzyCRfidcarPoistion::getCarCount, rfidPosition.getCarCounts()),
            eqParam(XzyCRfidcarPoistion::getTId, rfidPosition.getTId())
        );
        if (count > 0) {
            throw RestRequestException.normalFail("位置已注册，无法重复注册！");
        }
        List<Integer> carCounts = rfidPosition.getCarCounts();
        List<XzyCRfidcarPoistion> xzyCRfidCarPositions = new ArrayList<>();
        for (Integer carCount : carCounts) {
            XzyCRfidcarPoistion position = new XzyCRfidcarPoistion();
            BeanUtils.copyProperties(rfidPosition, position);
            position.setCarCount(carCount);
            position.setRuleId(UUID.randomUUID().toString());
            position.setCreateTime(new Date());
            position.setCreateUserCode(currentUser.getStaffId());
            position.setCreateUserName(currentUser.getName());
            xzyCRfidCarPositions.add(position);
        }
        xzyCRfidcarPoistionService.insertBatch(xzyCRfidCarPositions);
    }

    @Override
    public void addCritertion(XzyCRfidcarCritertion cRfidcarCritertion) {
        //判断位置是否已注册
        if (MybatisPlusUtils.selectExist(
            xzyCRfidcarCritertionService,
            eqParam(XzyCRfidcarCritertion::getCritertionId, cRfidcarCritertion.getCritertionId()),
            eqParam(XzyCRfidcarCritertion::getRepairPlaceCode, cRfidcarCritertion.getRepairPlaceCode())
        )) {
            throw RestRequestException.normalFail("标准关系已注册，无法重复注册！");
        }
        xzyCRfidcarCritertionService.insert(cRfidcarCritertion);
    }

    @Override
    public void delPosition(List<String> ids) {
        MybatisPlusUtils.delete(
            xzyCRfidcarPoistionService,
            inParam(XzyCRfidcarPoistion::getRuleId, ids)
        );
    }

    @Override
    public void delCritertion(List<String> ids) {
        MybatisPlusUtils.delete(
            xzyCRfidcarCritertionService,
            inParam(XzyCRfidcarCritertion::getId, ids)
        );
    }
}
