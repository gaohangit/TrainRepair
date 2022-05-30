package com.ydzbinfo.emis.trainRepair.common.util;

import com.ydzbinfo.emis.trainRepair.common.model.PostAndRole;
import com.ydzbinfo.emis.trainRepair.common.model.Role;
import com.ydzbinfo.emis.trainRepair.constant.RoleTypeEnum;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.Post;
import com.ydzbinfo.emis.utils.CommonUtils;

/**
 * 公共数据转换工具类
 *
 * @author 张天可
 * @since 2022/1/6
 */
public class CommonDataConvertUtil {

    public static PostAndRole toPostAndRole(Role role){
        PostAndRole postAndRole = new PostAndRole();
        CommonUtils.copyPropertiesToChild(
            role, Role.class,
            postAndRole, PostAndRole.class
        );
        postAndRole.setType(RoleTypeEnum.SYS_ROLE);
        return postAndRole;
    }

    public static PostAndRole toPostAndRole(Post post){
        PostAndRole postAndRole = new PostAndRole();
        postAndRole.setCode(post.getPostId());
        postAndRole.setName(post.getPostName());
        postAndRole.setType(RoleTypeEnum.POST_ROLE);
        return postAndRole;
    }
}
