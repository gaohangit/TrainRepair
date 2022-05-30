package com.ydzbinfo.emis.trainRepair.taskAllot.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.taskAllot.dao.PersonpostMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.PersonPost;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IPersonPostService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @since 2021-03-23
 */
@Service
public class PersonPostServiceImpl extends ServiceImpl<PersonpostMapper, PersonPost> implements IPersonPostService {
    @Resource
    PersonpostMapper personpostMapper;
    @Override
    public int addPersonpost(PersonPost personPost) {
        return personpostMapper.insert(personPost);
    }

    @Override
    public List<PersonPost> getPersonpostList(String staffId) {
        return personpostMapper.getPersonpostList(staffId);
    }
}
