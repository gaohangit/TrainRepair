package com.ydzbinfo.emis.trainRepair.taskAllot.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.taskAllot.dao.XzyBTaskallotshowDictMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyBTaskallotshowDict;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IXzyBTaskallotshowDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-10
 */
@Service
public class XzyBTaskallotshowDictServiceImpl extends ServiceImpl<XzyBTaskallotshowDictMapper, XzyBTaskallotshowDict> implements IXzyBTaskallotshowDictService {

    @Autowired
    XzyBTaskallotshowDictMapper xzyBTaskallotshowDictMapper;

    @Override
    public List<XzyBTaskallotshowDict> getShowDictByTaskAllotType(String taskAllotType) {
        return xzyBTaskallotshowDictMapper.getShowDictByTaskAllotType(taskAllotType);
    }
}
