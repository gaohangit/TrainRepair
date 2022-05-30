package com.ydzbinfo.emis.trainRepair.taskAllot.follower;

/**
 * @Description: 监听接收派工数据服务类
 * @Data: 2021/9/30
 * @Author: 冯帅
 */
// @Component
// @Conditional({SpringCloudStreamUtil.EnableReceiveCloudDataCondition.class})
public class TaskAllotFollowerService {

    // @StreamListener(target = MqSink.TASKALLOT_INPUT, condition = "headers['operateType']=='" + TaskAllotHeaderConstant.CREATE + "'")
    // public void receiveTaskAllotData(List<TaskAllotPacketEntity> taskAllotPacketList){
    //     if(!CollectionUtils.isEmpty(taskAllotPacketList)){
    //         //将数据插入到数据库中
    //         // xzyMTaskAllotService.setTaskAllotData(taskAllotPacketList);
    //     }
    //     System.out.println("接收成功");
    // }
}
