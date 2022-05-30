package com.ydzbinfo.emis.trainRepair.systemmanagent.messagemanagent.model;

public class MessageRec {
    private String messageId;
    private String userId;
    private String userName;
    private String recDate;
    private String recFlag;
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
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
	public String getRecDate() {
		return recDate;
	}
	public void setRecDate(String recDate) {
		this.recDate = recDate;
	}
	public String getRecFlag() {
		return recFlag;
	}
	public void setRecFlag(String recFlag) {
		this.recFlag = recFlag;
	}

	@Override
	public String toString() {
		return "MessageRec{" +
				"messageId='" + messageId + '\'' +
				", userId='" + userId + '\'' +
				", userName='" + userName + '\'' +
				", recDate='" + recDate + '\'' +
				", recFlag='" + recFlag + '\'' +
				'}';
	}
}
