package com.ydzbinfo.emis.trainRepair.taskAllot.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jxdinfo.hussar.core.mq.MessageUtil;
import com.jxdinfo.hussar.core.mq.MessageWrapper;
import com.ydzbinfo.emis.configs.kafka.taskallot.TaskAllotConfigMqSource;
import com.ydzbinfo.emis.trainRepair.common.model.PostAndRole;
import com.ydzbinfo.emis.trainRepair.common.model.Role;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.util.CommonDataConvertUtil;
import com.ydzbinfo.emis.trainRepair.repairmanagent.service.IXzyCWorkcritertionService;
import com.ydzbinfo.emis.trainRepair.taskAllot.constant.PostConfigHeaderConstant;
import com.ydzbinfo.emis.trainRepair.taskAllot.dao.PostMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.Post;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IPostService;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author gaohan
 * @since 2021-03-17
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements IPostService {

    protected static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    @Resource
    PostMapper postMapper;

    @Autowired(required = false)
    private TaskAllotConfigMqSource taskAllotConfigSource;

    @Resource
    IRemoteService remoteService;

    @Autowired
    private IXzyCWorkcritertionService xzyCWorkcritertionService;


    @Override
    public List<Post> getPostList(Page page) {
        List<Post> posts = baseMapper.getPostList(page);
        posts.sort((a, b) -> {
            int sortA = Integer.parseInt(a.getSort());
            int sortB = Integer.parseInt(b.getSort());
            if (sortA > sortB) {
                return 1;
            } else if (sortA < sortB) {
                return -1;
            } else {
                return 0;
            }
        });
        return posts;
    }

    @Override
    public Post getPosyById(String id) {
        return postMapper.selectById(id);
    }

    @Override
    public void addPost(Post post) {
        postMapper.insert(post);
    }

    @Override
    public void updPost(Post post) {
        postMapper.updateById(post);
    }

    @Override
    public String getMaxSort() {
        String maxSort = postMapper.getMaxSort();
        return StringUtils.isEmpty(maxSort) ? "0" : maxSort;
    }

    @Override
    public List<Post> getPostListById(String staffId) {
        return postMapper.getPostListById(staffId);
    }

    @Override
    public boolean sendData() {
        try {
            //向kafka通道中推送数据
            List<Post> postList = MybatisPlusUtils.selectList(
                postMapper,
                eqParam(Post::getFlag, "1")
            );
            if (!CollectionUtils.isEmpty(postList)) {
                MessageWrapper<List<Post>> messageWrapper = new MessageWrapper<>(PostConfigHeaderConstant.class, PostConfigHeaderConstant.CREATE, postList);
                boolean sendDataFlag = MessageUtil.sendMessage(taskAllotConfigSource.postconfigChannel(), messageWrapper);
                if (sendDataFlag) {
                    logger.info("岗位配置往kafka推送数据成功");
                } else {
                    logger.info("岗位配置往kafka推送数据失败");
                }
            }
        } catch (Exception ex) {
            logger.error("岗位配置往kafka推送数据出错：", ex);
        }
        return true;
    }

    @Override
    public List<PostAndRole> getPostAndRoleList() {
        //1.获取角色
        List<Role> roleList = remoteService.getAllRoleList();
        //2.获取岗位
        List<Post> postList = xzyCWorkcritertionService.getPostList();
        //3.组合数据
        List<PostAndRole> resList = new ArrayList<>();
        resList.addAll(postList.stream().map(CommonDataConvertUtil::toPostAndRole).collect(Collectors.toList()));
        resList.addAll(roleList.stream().map(CommonDataConvertUtil::toPostAndRole).collect(Collectors.toList()));
        return resList;
    }
}
