package com.ydzbinfo.emis.trainRepair.remotemodel.fault;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class AddQaVo {
    List<FaultQa> faultQaList;
    String dealWithId;
    String qaMan;
    String qaManCode;
    String qaResult;
    String resolveDeptCode;
    String qaTime;
    String qaOutComeDesc;
    String qaOutComeCode;
    String qaBranchCode;
    String qaBranchName;
    String qaComment;
    MultipartFile multipartFile;
}
