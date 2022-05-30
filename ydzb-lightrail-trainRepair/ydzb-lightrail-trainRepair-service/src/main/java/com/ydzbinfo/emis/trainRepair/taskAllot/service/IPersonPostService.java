package com.ydzbinfo.emis.trainRepair.taskAllot.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.PersonPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @since 2021-03-23
 */
public interface IPersonPostService extends IService<PersonPost> {
    int addPersonpost(PersonPost personPost);

    //获取岗位
    List<PersonPost> getPersonpostList(@Param("staffId") String  staffId);
}
