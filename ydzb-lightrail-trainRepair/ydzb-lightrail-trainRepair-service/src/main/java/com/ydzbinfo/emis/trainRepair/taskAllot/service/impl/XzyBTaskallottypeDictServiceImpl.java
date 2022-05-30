package com.ydzbinfo.emis.trainRepair.taskAllot.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.taskAllot.dao.XzyBTaskallottypeDictMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyBTaskallottypeDict;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IXzyBTaskallottypeDictService;
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
public class XzyBTaskallottypeDictServiceImpl extends ServiceImpl<XzyBTaskallottypeDictMapper, XzyBTaskallottypeDict> implements IXzyBTaskallottypeDictService {

    @Autowired
    XzyBTaskallottypeDictMapper taskallottypeDictMapper;

    @Override
    public List<XzyBTaskallottypeDict> getTaskAllotTypeDict() {
        return taskallottypeDictMapper.getTaskAllotTypeDict();
    }

    @Override
    public XzyBTaskallottypeDict getTaskAllotTypeByCode(String code) {
        return taskallottypeDictMapper.getTaskAllotTypeByCode(code);
    }
}
