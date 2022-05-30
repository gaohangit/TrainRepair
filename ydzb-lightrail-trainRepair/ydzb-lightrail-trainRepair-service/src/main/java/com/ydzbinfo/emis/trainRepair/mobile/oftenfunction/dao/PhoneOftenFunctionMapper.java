package com.ydzbinfo.emis.trainRepair.mobile.oftenfunction.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.mobile.querymodel.PhoneOftenFunction;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 韩旭
 * @since 2021-04-07
 */
public interface PhoneOftenFunctionMapper extends BaseMapper<PhoneOftenFunction> {
    //获取常用功能
    List<String> getOftenFunction(@Param("staffId") String staffId,@Param("type") String type);
    //设置常用功能
    int setOftenFunction(@Param("list") List<PhoneOftenFunction> list);
    //删除常用功能
    int delOftenFunction(@Param("staffId") String staffID,
                         @Param("delUserName") String delUserName,
                         @Param("delTime") Date delTime,
                         @Param("delUserCode") String delUserCode);
}
