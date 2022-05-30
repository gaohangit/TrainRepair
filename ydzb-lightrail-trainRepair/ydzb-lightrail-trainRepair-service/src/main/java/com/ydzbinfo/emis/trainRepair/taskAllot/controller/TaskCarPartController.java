package com.ydzbinfo.emis.trainRepair.taskAllot.controller;

import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotdept;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotperson;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskcarpart;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskAllotDeptService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskAllotPersonService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskallotpacketService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskcarpartService;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author: duanzefan
 * @Date: 2020/9/20 15:22
 * @Description: 派工任务管理
 */
@Controller
@RequestMapping("/xzyMTaskCarPart")
public class TaskCarPartController {

	protected static final Logger logger = LoggerFactory.getLogger(TaskCarPartController.class);

	@Autowired
    XzyMTaskcarpartService taskcarpartService;
	@Autowired
    XzyMTaskAllotPersonService taskAllotPersonService;
	@Autowired
    XzyMTaskAllotDeptService taskAllotDeptService;
	@Autowired
    XzyMTaskallotpacketService taskallotpacketService;

	/**
	 * @author: duanzefan
	 * @Date: 2020/9/20 15:25
	 * @Description: 通过派工包获取派工任务
	 */
	@ApiOperation("通过派工包获取派工任务")
	@RequestMapping(value = "/getTaskAllotByPacket")
	@ResponseBody
	@BussinessLog(value = "通过派工包获取派工任务", key = "/xzyMTaskCarPart/getTaskAllotByPacket", type = "04")
	public List<XzyMTaskcarpart> getTaskAllotByPacket(String packetId) {
		return taskcarpartService.getTaskAllotListByPacket(packetId);
	}

	/**
	 * @author: duanzefan
	 * @Date: 2020/9/22 18:32
	 * @Description: 根据条件查询派工任务
	 */
	@ApiOperation("根据条件查询派工任务")
	@RequestMapping(value = "/getCarPartListByParam")
	@ResponseBody
	@BussinessLog(value = "根据条件查询派工任务", key = "/xzyMTaskCarPart/getCarPartListByParam", type = "04")
	public List<XzyMTaskcarpart> getCarPartListByParam(@RequestBody Map<String, String> map) {
		List<XzyMTaskcarpart> taskcarpartList = taskcarpartService.getCarPartListByParam(map);
		for (XzyMTaskcarpart taskcarpart : taskcarpartList) {
			List<XzyMTaskallotdept> taskallotdeptList = taskAllotDeptService.getTaskAllotDeptByCarPart(taskcarpart.getProcessId());
			if (taskallotdeptList != null && taskallotdeptList.size() > 0) {
				XzyMTaskallotdept taskallotdept = taskallotdeptList.get(0);
				taskcarpart.setXzyMTaskallotdept(taskallotdept);
			}
			List<XzyMTaskallotperson> taskallotpersonList = taskAllotPersonService.getTaskAllotPersonListByProcessId(taskcarpart.getProcessId());
			taskcarpart.setWorkerList(taskallotpersonList);
		}
		return taskcarpartList;
	}
}
