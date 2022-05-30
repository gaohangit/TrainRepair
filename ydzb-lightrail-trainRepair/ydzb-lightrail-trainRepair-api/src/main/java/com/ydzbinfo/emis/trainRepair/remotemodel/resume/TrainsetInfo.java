package com.ydzbinfo.emis.trainRepair.remotemodel.resume;

public class TrainsetInfo {

    /**
     * 四位车号，例如：1001
     */
    private String trainsetno;

    /**
     * 配属局编号，例如：R
     */
    private String burea;

    /**
     * 配属段编号，例如：00556
     */
    private String depot;

    /**
     * 配属所编号，例如：001
     */
    private String deparment;

    /**
     * 运用状态编号，取自车组状态表中的运用状态，00为未知，01为运用，02为热备，03为备用，05为检修试运，06为线路试运，04为临修，07为检修回送，08为运用回送，09为待检，10为停放，11为三级修，12为四级修，13为五级修，14为新配，15为二级(专项)修
    **/
    private String usestatus;

    /**
     * 使用标志，取自车组状态表中的使用标志，01本属运用，02本属借出，11外属借入，13外属检修。说明本所维护该车状态的理由
    **/
    private String borrowflag;

    //车组当前走行公里
    private String accumile;


    //制造单位，例如“BST”、“长客”、“四厂”、“唐厂”
    private String sManufacture;

    //出厂日期，例如：2006/10/18
    private java.sql.Date dProducedate;

    //编组数量，例如8、16、17、6、4
    private String iMarshalcount;
    //编组形式，例如：“Mc1+Tp1+M1+M3+Tb+M2+Tp2+Mc2”
    private String sMarshaltype;

    //图号，例如：“790-1”
    private String sPlotnum;
    //车组软件版本，例如：“EM-934-0107-2”
    private String sTrainsetsoftversion;
    //列车总长，例如：213.5
    private String iLength;
    //列车最大宽度，例如：3328
    private String iMaxwidth;
    //车厢最大高度，例如：4040
    private String iMaxheight;
    //车体材质
    private String sBodymaterial;
    //车组设计寿命
    private String iDesignlife;
    //适应站台高度
    private String sPlatheight;
    //编组总重
    private String fWeight;
    //全列载重
    private String fLoadweight;
    //总定员
    private String iPassengercount;
    //最高试验速度
    private String iMaxspeed;
    //标记速度
    private String iTagspeed;
    //转向架中心距
    private String iBogcentredistance;
    //车辆固定轴距
    private String iWheelbase;
    //车轮直径
    private String iDiametre;
    //车轮踏面类型
    private String iTreadtype;
    //最大轴重
    private String iAxleweight;
    //自动车钩中心高度
    private String iAutodrawgearheight;
    //中间车钩中心高度
    private String iMiddrawgearheight;
    //牵引总功率
    private String iPower;
    // ATP型号
    private String sAtpmodule;
    //最大制动距离
    private String iMaxbrakedistance;
    //停放制动
    private String sParkbrake;
    //有无LKJ
    private String sExistlkj;
    //车组通过最小半径
    private String iMinradius;
    // S曲线最小半径
    private String iSminradius;
    //受电弓最大升弓高度
    private String iPantomaxheight;
    //受电弓工作高度
    private String iPantoworkheight;
    //受电弓落弓高度
    private String iPantoheight;
    //蓄电池电压
    private String iGarnervoltage;
    //外接电源电压
    private String iExvoltage;
    //正常运行网压
    private String sCommonvoltage;
    //环境温度
    private String iEnvtemp;
    //填报人
    private String sRecordman;
    //填报时间
    private java.sql.Date dRecordtime;
    //填报主机厂ID
    private String sFactoryid;
    //受电弓监控系统型号
    private String iPantovideotype;
    //车组级加装改造备注信息
    private String sRemark;


    //源自车组字典表
    //车组用途属性(1 运营，2 路用)
    private String sUseattributeid;
    //资产属性ID(1是国铁，2是合资公司，3是地方铁路，４是临时配属车)
    private String sEquityattributeid;
    //资产公司ID(E001为广深公司，E002为大秦公司，E003为广珠城际，E004为湖南城际，E005为珠三角城际，E006为宁波城际
    private String sEqcomid;

    //批次
    private String traintempid;
    //车型
    private String traintype;
    
    public String getTrainsetno() {
        return trainsetno;
    }

    public void setTrainsetno(String trainsetno) {
        this.trainsetno = trainsetno;
    }

    public String getBurea() {
        return burea;
    }

    public void setBurea(String burea) {
        this.burea = burea;
    }


    public String getDepot() {
        return depot;
    }

    public void setDepot(String depot) {
        this.depot = depot;
    }

    public String getDeparment() {
        return deparment;
    }

    public void setDeparment(String deparment) {
        this.deparment = deparment;
    }


    public String getUsestatus() {
        return usestatus;
    }

    public void setUsestatus(String usestatus) {
        this.usestatus = usestatus;
    }

    public String getBorrowflag() {
        return borrowflag;
    }

    public void setBorrowflag(String borrowflag) {
        this.borrowflag = borrowflag;
    }


    public String getAccumile() {
        return accumile;
    }

    public void setAccumile(String accumile) {
        this.accumile = accumile;
    }

    public String getSManufacture() {
        return sManufacture;
    }

    public void setSManufacture(String sManufacture) {
        this.sManufacture = sManufacture;
    }


    public java.sql.Date getDProducedate() {
        return dProducedate;
    }

    public void setDProducedate(java.sql.Date dProducedate) {
        this.dProducedate = dProducedate;
    }


    public String getIMarshalcount() {
        return iMarshalcount;
    }

    public void setIMarshalcount(String iMarshalcount) {
        this.iMarshalcount = iMarshalcount;
    }


    public String getSMarshaltype() {
        return sMarshaltype;
    }

    public void setSMarshaltype(String sMarshaltype) {
        this.sMarshaltype = sMarshaltype;
    }


    public String getSPlotnum() {
        return sPlotnum;
    }

    public void setSPlotnum(String sPlotnum) {
        this.sPlotnum = sPlotnum;
    }


    public String getSTrainsetsoftversion() {
        return sTrainsetsoftversion;
    }

    public void setSTrainsetsoftversion(String sTrainsetsoftversion) {
        this.sTrainsetsoftversion = sTrainsetsoftversion;
    }


    public String getILength() {
        return iLength;
    }

    public void setILength(String iLength) {
        this.iLength = iLength;
    }


    public String getIMaxwidth() {
        return iMaxwidth;
    }

    public void setIMaxwidth(String iMaxwidth) {
        this.iMaxwidth = iMaxwidth;
    }


    public String getIMaxheight() {
        return iMaxheight;
    }

    public void setIMaxheight(String iMaxheight) {
        this.iMaxheight = iMaxheight;
    }


    public String getSBodymaterial() {
        return sBodymaterial;
    }

    public void setSBodymaterial(String sBodymaterial) {
        this.sBodymaterial = sBodymaterial;
    }


    public String getIDesignlife() {
        return iDesignlife;
    }

    public void setIDesignlife(String iDesignlife) {
        this.iDesignlife = iDesignlife;
    }


    public String getSPlatheight() {
        return sPlatheight;
    }

    public void setSPlatheight(String sPlatheight) {
        this.sPlatheight = sPlatheight;
    }


    public String getFWeight() {
        return fWeight;
    }

    public void setFWeight(String fWeight) {
        this.fWeight = fWeight;
    }


    public String getFLoadweight() {
        return fLoadweight;
    }

    public void setFLoadweight(String fLoadweight) {
        this.fLoadweight = fLoadweight;
    }


    public String getIPassengercount() {
        return iPassengercount;
    }

    public void setIPassengercount(String iPassengercount) {
        this.iPassengercount = iPassengercount;
    }


    public String getIMaxspeed() {
        return iMaxspeed;
    }

    public void setIMaxspeed(String iMaxspeed) {
        this.iMaxspeed = iMaxspeed;
    }


    public String getITagspeed() {
        return iTagspeed;
    }

    public void setITagspeed(String iTagspeed) {
        this.iTagspeed = iTagspeed;
    }


    public String getIBogcentredistance() {
        return iBogcentredistance;
    }

    public void setIBogcentredistance(String iBogcentredistance) {
        this.iBogcentredistance = iBogcentredistance;
    }


    public String getIWheelbase() {
        return iWheelbase;
    }

    public void setIWheelbase(String iWheelbase) {
        this.iWheelbase = iWheelbase;
    }


    public String getIDiametre() {
        return iDiametre;
    }

    public void setIDiametre(String iDiametre) {
        this.iDiametre = iDiametre;
    }


    public String getITreadtype() {
        return iTreadtype;
    }

    public void setITreadtype(String iTreadtype) {
        this.iTreadtype = iTreadtype;
    }


    public String getIAxleweight() {
        return iAxleweight;
    }

    public void setIAxleweight(String iAxleweight) {
        this.iAxleweight = iAxleweight;
    }


    public String getIAutodrawgearheight() {
        return iAutodrawgearheight;
    }

    public void setIAutodrawgearheight(String iAutodrawgearheight) {
        this.iAutodrawgearheight = iAutodrawgearheight;
    }


    public String getIMiddrawgearheight() {
        return iMiddrawgearheight;
    }

    public void setIMiddrawgearheight(String iMiddrawgearheight) {
        this.iMiddrawgearheight = iMiddrawgearheight;
    }


    public String getIPower() {
        return iPower;
    }

    public void setIPower(String iPower) {
        this.iPower = iPower;
    }


    public String getSAtpmodule() {
        return sAtpmodule;
    }

    public void setSAtpmodule(String sAtpmodule) {
        this.sAtpmodule = sAtpmodule;
    }


    public String getIMaxbrakedistance() {
        return iMaxbrakedistance;
    }

    public void setIMaxbrakedistance(String iMaxbrakedistance) {
        this.iMaxbrakedistance = iMaxbrakedistance;
    }


    public String getSParkbrake() {
        return sParkbrake;
    }

    public void setSParkbrake(String sParkbrake) {
        this.sParkbrake = sParkbrake;
    }


    public String getSExistlkj() {
        return sExistlkj;
    }

    public void setSExistlkj(String sExistlkj) {
        this.sExistlkj = sExistlkj;
    }


    public String getIMinradius() {
        return iMinradius;
    }

    public void setIMinradius(String iMinradius) {
        this.iMinradius = iMinradius;
    }


    public String getISminradius() {
        return iSminradius;
    }

    public void setISminradius(String iSminradius) {
        this.iSminradius = iSminradius;
    }


    public String getIPantomaxheight() {
        return iPantomaxheight;
    }

    public void setIPantomaxheight(String iPantomaxheight) {
        this.iPantomaxheight = iPantomaxheight;
    }


    public String getIPantoworkheight() {
        return iPantoworkheight;
    }

    public void setIPantoworkheight(String iPantoworkheight) {
        this.iPantoworkheight = iPantoworkheight;
    }


    public String getIPantoheight() {
        return iPantoheight;
    }

    public void setIPantoheight(String iPantoheight) {
        this.iPantoheight = iPantoheight;
    }


    public String getIGarnervoltage() {
        return iGarnervoltage;
    }

    public void setIGarnervoltage(String iGarnervoltage) {
        this.iGarnervoltage = iGarnervoltage;
    }


    public String getIExvoltage() {
        return iExvoltage;
    }

    public void setIExvoltage(String iExvoltage) {
        this.iExvoltage = iExvoltage;
    }


    public String getSCommonvoltage() {
        return sCommonvoltage;
    }

    public void setSCommonvoltage(String sCommonvoltage) {
        this.sCommonvoltage = sCommonvoltage;
    }


    public String getIEnvtemp() {
        return iEnvtemp;
    }

    public void setIEnvtemp(String iEnvtemp) {
        this.iEnvtemp = iEnvtemp;
    }


    public String getSRecordman() {
        return sRecordman;
    }

    public void setSRecordman(String sRecordman) {
        this.sRecordman = sRecordman;
    }


    public java.sql.Date getDRecordtime() {
        return dRecordtime;
    }

    public void setDRecordtime(java.sql.Date dRecordtime) {
        this.dRecordtime = dRecordtime;
    }


    public String getSFactoryid() {
        return sFactoryid;
    }

    public void setSFactoryid(String sFactoryid) {
        this.sFactoryid = sFactoryid;
    }


    public String getIPantovideotype() {
        return iPantovideotype;
    }

    public void setIPantovideotype(String iPantovideotype) {
        this.iPantovideotype = iPantovideotype;
    }


    public String getSRemark() {
        return sRemark;
    }

    public void setSRemark(String sRemark) {
        this.sRemark = sRemark;
    }


    public String getSUseattributeid() {
        return sUseattributeid;
    }

    public void setSUseattributeid(String sUseattributeid) {
        this.sUseattributeid = sUseattributeid;
    }


    public String getSEquityattributeid() {
        return sEquityattributeid;
    }

    public void setSEquityattributeid(String sEquityattributeid) {
        this.sEquityattributeid = sEquityattributeid;
    }


    public String getSEqcomid() {
        return sEqcomid;
    }

    public void setSEqcomid(String sEqcomid) {
        this.sEqcomid = sEqcomid;
    }

	public String getTraintempid() {
		return traintempid;
	}

	public void setTraintempid(String traintempid) {
		this.traintempid = traintempid;
	}

	public String getTraintype() {
		return traintype;
	}

	public void setTraintype(String traintype) {
		this.traintype = traintype;
	}
    
    

}
