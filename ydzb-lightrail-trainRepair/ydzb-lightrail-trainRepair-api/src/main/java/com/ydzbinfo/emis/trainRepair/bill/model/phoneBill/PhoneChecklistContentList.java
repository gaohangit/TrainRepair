package com.ydzbinfo.emis.trainRepair.bill.model.phoneBill;

import lombok.Data;

import java.util.List;

@Data
public class PhoneChecklistContentList {

    //是否显示回填默认值按钮 true：显示  false：不显示
    private boolean isShowDefaultButton;

    //回填默认值按钮是否只读（只有isShowDefaultButton为true时有效） true：只读 false：可操作
    private boolean isDefaultButtonReadOnly;

    //该层级下所有的单元格是否回填完毕,为空和为false时表示未回填完毕,只有为true时算回填完毕
    private boolean isAllFill;

    //回填描述
    private PhoneChecklistDetailInfo titleInfo;

    //回填内容
    private List<PhoneChecklistDetailInfo> detailInfoList;

    //检修人员签字
    private List<PhoneChecklistDetailInfo> signList;

    //下一层数据  为空则说明没有下一层
    private List<PhoneChecklistContentList> contentLists;
}
