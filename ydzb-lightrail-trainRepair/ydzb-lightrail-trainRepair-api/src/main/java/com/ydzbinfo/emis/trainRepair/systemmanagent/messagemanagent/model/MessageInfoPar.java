package com.ydzbinfo.emis.trainRepair.systemmanagent.messagemanagent.model;

public class MessageInfoPar {
	private String type;
	private String subject;
	private String appCode;
	private String moduleCode;
	private String startDate;
	private String endDate;
	private String recFlag;
	private String userId;
	  /**
     * 每页限定显示条数
     */
    private int limit = 100;

    /**
     * 当前页
     */
    private int page = 1;
    
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getRecFlag() {
		return recFlag;
	}

	public void setRecFlag(String recFlag) {
		this.recFlag = recFlag;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
	
	
}
