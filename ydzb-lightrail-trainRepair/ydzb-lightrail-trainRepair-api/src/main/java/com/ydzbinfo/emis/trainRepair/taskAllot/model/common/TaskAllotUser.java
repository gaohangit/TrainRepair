package com.ydzbinfo.emis.trainRepair.taskAllot.model.common;

import com.ydzbinfo.emis.trainRepair.common.model.User;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.PostModel;
import lombok.Data;

import java.util.List;

@Data
public class TaskAllotUser extends User {

	//岗位
	List<PostModel> postModelList;

}
