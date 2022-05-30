package com.ydzbinfo.emis.trainRepair.taskAllot.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.common.model.PostAndRole;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.Post;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-03-17
 */
public interface IPostService extends IService<Post> {
    List<Post> getPostList(Page page);

    Post getPosyById(String id);

    void addPost(Post post);

    void updPost(Post post);

    String getMaxSort();


    List<Post> getPostListById(@Param("staffId") String  staffId);

    boolean sendData();

    List<PostAndRole> getPostAndRoleList();
}
