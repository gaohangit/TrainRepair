package com.ydzbinfo.emis.trainRepair.taskAllot.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.configs.kafka.taskallot.TaskAllotConfigMqSource;
import com.ydzbinfo.emis.trainRepair.common.model.PostAndRole;
import com.ydzbinfo.emis.trainRepair.common.model.UserShiroInfo;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.Post;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.IPostService;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.result.RestResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author 高晗
 * @description 班组岗位类型配置
 * @createDate 2021/3/17 10:01
 **/
@RestController
@RequestMapping("post")
public class PostController {
    protected static final Logger logger = LoggerFactory.getLogger(PostController.class);

        @Resource
    private IPostService postService;

    @ApiOperation("获取所有班组岗位")
    @GetMapping(value = "/getPostList")
    public Object getPostList(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        try {
            if(pageSize==-1){
                pageSize=Integer.MAX_VALUE;
            }
            Page page = new Page(pageNum, pageSize);
            List<Post> postList = postService.getPostList(page);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("postList", postList);
            jsonObject.put("count", page.getTotal());
            return RestResult.success().setData(jsonObject);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取所有班组岗位错误");
        }
    }

    @ApiOperation("获取班组岗位")
    @GetMapping(value = "/getPosyById")
    public Object getPosyById(String id) {
        try {
            Post postList = postService.getPosyById(id);
            return RestResult.success().setData(postList);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "获取班组岗位错误");
        }
    }


    @ApiOperation("新增班组岗位")
    @PostMapping(value = "/addPost")
    public Object addPost(@RequestBody Post post) {
        try {
            UserShiroInfo shiroUtil = UserShiroUtil.getUserInfo();
            post.setFlag("1");
            post.setCreateTime(new Date());
            post.setCreateUserCode(UserUtil.getUserInfo().getStaffId());
            post.setCreateUserName(UserUtil.getUserInfo().getName());
            post.setSort(String.valueOf(Integer.parseInt(postService.getMaxSort()) + 1));
            postService.addPost(post);
            if (SpringCloudStreamUtil.enableSendCloudData(TaskAllotConfigMqSource.class)) {
                //向kafka通道中推送数据
                postService.sendData();
            }
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "新增班组岗位错误");
        }
    }

    @ApiOperation("修改班组岗位")
    @PostMapping(value = "/updPost")
    public Object updPost(@RequestBody List<Post> posts) {
        try {
            for (Post post : posts) {
                postService.updPost(post);
            }
            if (SpringCloudStreamUtil.enableSendCloudData(TaskAllotConfigMqSource.class)) {
                //向kafka通道中推送数据
                postService.sendData();
            }
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "修改班组岗位错误");
        }
    }

    @ApiOperation("删除班组岗位")
    @PostMapping(value = "/delPost")
    public Object delPost(@RequestBody Post post) {
        try {
            post.setFlag("0");
            post.setDelTime(new Date());
            post.setDelUserCode(UserUtil.getUserInfo().getStaffId());
            post.setDelUserName(UserUtil.getUserInfo().getName());
            postService.updPost(post);
            if (SpringCloudStreamUtil.enableSendCloudData(TaskAllotConfigMqSource.class)) {
                //向kafka通道中推送数据
                postService.sendData();
            }
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "删除班组岗位错误");
        }
    }

    @ApiOperation(value = "获取岗位+角色下拉框")
    @GetMapping(value = "/gatPostAndRoleList")
    @ResponseBody
    public RestResult gatPostAndRoleList() {
        try {
            List<PostAndRole> postAndRoles = postService.getPostAndRoleList();
            return RestResult.success().setData(postAndRoles);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "gatPostAndRoleList");
        }
    }
}
