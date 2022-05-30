package com.ydzbinfo.emis.trainRepair.taskAllot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.Post;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-03-17
 */
public interface PostMapper extends BaseMapper<Post> {
    List<Post> getPostList(Page page);

    List<Post> getPostListById(@Param("staffId") String  staffId);

    String getMaxSort();

}
