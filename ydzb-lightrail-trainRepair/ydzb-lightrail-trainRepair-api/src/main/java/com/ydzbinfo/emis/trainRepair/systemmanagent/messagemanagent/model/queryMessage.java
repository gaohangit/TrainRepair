package com.ydzbinfo.emis.trainRepair.systemmanagent.messagemanagent.model;

public class queryMessage {
	/**
	 * 用户账户名称
	 **/
	private String userAccount;


	
	/**
	 * 开始时间
	 **/
	private String startdate;

	/**
	 * 开始时间
	 **/
	private String enddate;
	
	/**
	 * 消息类型
	 **/
	private String type;
	

	/**
	 * 状态
	 **/
	private String state;

	

	private int limit;

	private int page;

	
	/**
	 * 排序字段
	 */
	private String orderInfo;

	/**
	 * 排序方式
	 */
	private String orderAscOrDesc;



	public String getStartdate() {
		return startdate;
	}


	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}


	public String getEnddate() {
		return enddate;
	}


	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
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


	public String getOrderInfo() {
		return orderInfo;
	}


	public void setOrderInfo(String orderInfo) {
		this.orderInfo = orderInfo;
	}


	public String getOrderAscOrDesc() {
		return orderAscOrDesc;
	}


	public void setOrderAscOrDesc(String orderAscOrDesc) {
		this.orderAscOrDesc = orderAscOrDesc;
	}


	public String getUserAccount() {
		return userAccount;
	}


	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	
	
	
}
