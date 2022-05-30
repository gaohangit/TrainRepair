package com.ydzbinfo.emis.trainRepair.systemmanagent.messagemanagent.model;
/**
 * 消息管理
 * **/
public class message {

	
	/**
	 * 消息id
	 **/
	private String id;
	
	/**
	 * 消息内容
	 **/
	private String content;
	
	/**
	 * 消息发送人
	 **/
	private String sender;
	
	/**
	 * 查看时间
	 **/
	private String time;


	/**
	 * 消息类型
	 **/
	private String type;
	
	/**
	 * 消息类型id
	 **/
	private String typeID;

	/**
	 * 状态
	 **/
	private String state;



	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
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


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getTypeID() {
		return typeID;
	}

	public void setTypeID(String typeID) {
		this.typeID = typeID;
	}

	
}
