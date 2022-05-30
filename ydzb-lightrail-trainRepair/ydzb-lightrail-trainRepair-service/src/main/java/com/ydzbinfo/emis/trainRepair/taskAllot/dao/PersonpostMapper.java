package com.ydzbinfo.emis.trainRepair.taskAllot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.PersonPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @since 2021-03-23
 */
public interface PersonpostMapper extends BaseMapper<PersonPost> {
    //获取岗位
    List<PersonPost> getPersonpostList(@Param("staffId") String  staffId);



}
