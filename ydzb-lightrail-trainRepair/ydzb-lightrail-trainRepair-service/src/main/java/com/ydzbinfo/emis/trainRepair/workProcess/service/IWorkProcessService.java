package com.ydzbinfo.emis.trainRepair.workProcess.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.workprocess.model.*;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessLocation;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPic;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessTimeRecord;
import com.ydzbinfo.emis.utils.upload.UploadedFileInfo;

import java.util.List;
import java.util.Map;


public interface IWorkProcessService {

	/**
	 * @author: 冯帅
	 * @Date: 2021/5/8
	 * @Description: 查询一级修列表
	 */
	List<QueryOneWorkProcessData> getOneWorkProcessList(QueryOneWorkProcessData queryOneWorkProcessData);


	/**
	 * @author: 冯帅
	 * @Date: 2021/5/8
	 * @Description: 添加一级修作业过程
	 */
	boolean addOneWorkProcess(OneWorkProcessData oneWorkProcessData,List<UploadedFileInfo> uploadedFileInfos);

	/**
	 * @author: 冯帅
	 * @Date: 2021/5/8
	 * @Description: 删除一级修作业过程
	 */
	boolean delOneWorkProcess(OneWorkProcessData oneWorkProcessData);


	/**
	 * @author: 冯帅
	 * @Date: 2021/5/8
	 * @Description: 插入作业时间记录表
	 */
	boolean addProcessTimeRecordList(List<ProcessTimeRecord> processTimeRecordList);

	/**
	 * @author: 冯帅
	 * @Date: 2021/5/8
	 * @Description: 插入作业过程定位表
	 */
	boolean addProcessLocationList(List<ProcessLocation> processLocationList);

	/**
	 * @author: 冯帅
	 * @Date: 2021/5/8
	 * @Description: 插入作业过程图片表
	 */
	boolean addProcessPicList(List<ProcessPic> processPicList);

	/**
	 * @author: 冯帅
	 * @Date: 2021/5/8
	 * @Description: 查询二级修列表
	 */
	Page<QueryTwoWorkProcessData> getTwoWorkProcessList(TwoWorkProcessData twoWorkProcessData);

	/**
	 * @author: 冯帅
	 * @Date: 2021/5/8
	 * @Description: 查询二级修列表
	 */
	Page<QueryTwoWorkProcess> getTwoWorkProcessData(TwoWorkProcess twoWorkProcess);

	/**
	 * @author: 冯帅
	 * @Date: 2021/5/8
	 * @Description: 添加二级修作业过程
	 */
	boolean addTwoWorkProcess(TwoWorkProcessData twoWorkProcessData,List<UploadedFileInfo> uploadedFileInfos);

	/**
	 * @author: 冯帅
	 * @Date: 2021/5/8
	 * @Description: 删除二级修作业过程
	 */
	boolean delTwoWorkProcess(TwoWorkProcessData twoWorkProcessData);

	/**
	 * @author: 冯帅
	 * @Date: 2021/5/8
	 * @Description: 获取一体化查询列表
	 */
	List<IntegrationProcessData> getIntegrationList(ProcessData processData);

	/**
	 * @author: 冯帅
	 * @Date: 2021/5/8
	 * @Description: 无修程作业过程确认
	 */
	boolean addIntegration(IntegrationProcessData integrationProcessData,String phoneFlag);

	/**
	 * @author: 冯帅
	 * @Date: 2021/5/8
	 * @Description: 删除无修程作业过程
	 */
	boolean delIntegration(IntegrationProcessData integrationProcessData);

	/**
	 * @author: 冯帅
	 * @Date: 2021/12/10
	 * @Description: 无修程作业过程获取车组集合
	 */
	List<Map<String,String>> getIntegrationTrainsetList(String unitCode,String dayPlanId,String repairType);

	/**
	 * @author: 冯帅
	 * @Date: 2021/12/10
	 * @Description: 无修程作业过程作业任务集合
	 */
	List<ZtTaskPacketEntity> getIntegrationTaskList(String dayPlanId,String trainsetId,String repairDeptCode,String deptCode,String lstPacketTypeCode);
}
