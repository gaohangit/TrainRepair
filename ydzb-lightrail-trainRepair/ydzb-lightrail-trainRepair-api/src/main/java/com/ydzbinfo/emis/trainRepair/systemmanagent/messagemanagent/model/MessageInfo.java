package com.ydzbinfo.emis.trainRepair.systemmanagent.messagemanagent.model;

import java.util.List;

public class MessageInfo {
	private String messageId;
	private String type;
	private String subject;
	private String content;
	private String recType;
	private String expires;
	private String targetUrl;
	private String userId;
	private String userName;
	private String appCode;
	private String appName;
	private String moduleCode;
	private String moduleName;
	private String addDate;
	
    private String recFlag;

	private List<MessageRec> recList;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRecType() {
		return recType;
	}

	public void setRecType(String recType) {
		this.recType = recType;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public List<MessageRec> getRecList() {
		return recList;
	}

	public void setRecList(List<MessageRec> recList) {
		this.recList = recList;
	}

	public String getAddDate() {
		return addDate;
	}

	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}

	public String getRecFlag() {
		return recFlag;
	}

	public void setRecFlag(String recFlag) {
		this.recFlag = recFlag;
	}

	@Override
	public String toString() {
		return "MessageInfo{" +
				"messageId='" + messageId + '\'' +
				", type='" + type + '\'' +
				", subject='" + subject + '\'' +
				", content='" + content + '\'' +
				", recType='" + recType + '\'' +
				", expires='" + expires + '\'' +
				", targetUrl='" + targetUrl + '\'' +
				", userId='" + userId + '\'' +
				", userName='" + userName + '\'' +
				", appCode='" + appCode + '\'' +
				", appName='" + appName + '\'' +
				", moduleCode='" + moduleCode + '\'' +
				", moduleName='" + moduleName + '\'' +
				", addDate='" + addDate + '\'' +
				", recFlag='" + recFlag + '\'' +
				", recList=" + recList +
				'}';
	}
}
