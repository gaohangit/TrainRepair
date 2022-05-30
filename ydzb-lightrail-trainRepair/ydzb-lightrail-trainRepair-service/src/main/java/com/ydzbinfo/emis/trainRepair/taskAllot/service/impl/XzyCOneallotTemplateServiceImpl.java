package com.ydzbinfo.emis.trainRepair.taskAllot.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.taskAllot.dao.XzyCOneallotTemplateMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyCOneallotTemplate;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IXzyCOneallotTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-10
 */
@Service
public class XzyCOneallotTemplateServiceImpl extends ServiceImpl<XzyCOneallotTemplateMapper, XzyCOneallotTemplate> implements IXzyCOneallotTemplateService {

    @Autowired
    XzyCOneallotTemplateMapper xzyCOneallotTemplateMapper;

    @Autowired
    IXzyCOneallotTemplateService oneallotTemplateService;

    @Override
    public List<XzyCOneallotTemplate> getTemplateListByParam(Map<String,String> map) {
        return xzyCOneallotTemplateMapper.getTemplateListByParam(map);
    }

    @Override
    public List<XzyCOneallotTemplate> getAllTemplateByMarshalNum(String marshalNum) {
        Map<String, Object> result = new HashMap<>();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("marshalNum", marshalNum);
        List<XzyCOneallotTemplate> oneallotTemplateList = oneallotTemplateService.getTemplateListByParam(paramMap);
        List<XzyCOneallotTemplate> oneallotTemplates = new ArrayList<>();
        //遍历模板集合
        for (int i = 0; i < oneallotTemplateList.size(); i++) {
            int last = oneallotTemplateList.size() - 1;
            //第一次直接添加到新集合里
            if (i == 0) {
                oneallotTemplates.add(oneallotTemplateList.get(i));
            }
            //非第一次
            if (i > 0) {
                //此次的分组id与上一次相同
                if (oneallotTemplateList.get(i).getsGroupid().equals(oneallotTemplateList.get(i - 1).getsGroupid())) {
                    String carNoList = oneallotTemplateList.get(i - 1).getsCarnolist();
                    carNoList += "," + oneallotTemplateList.get(i).getsCarnolist();
                    oneallotTemplateList.get(i).setsCarnolist(carNoList);
                    //此次的与上一次的不同
                } else if (!oneallotTemplateList.get(i).getsGroupid().equals(oneallotTemplateList.get(i - 1).getsGroupid())) {
                    boolean canadd = true;
                    for (XzyCOneallotTemplate oneallotTemplate : oneallotTemplates) {
                        if (oneallotTemplate.getsGroupid().equals(oneallotTemplateList.get(i - 1).getsGroupid())) {
                            canadd = false;
                        }
                    }
                    if (canadd) {
                        oneallotTemplates.add(oneallotTemplateList.get(i - 1));
                    }
                    //最后一次
                }
                if (i == last) {
                    oneallotTemplates.add(oneallotTemplateList.get(i));
                }
            }
        }
        return oneallotTemplates;
    }
}
