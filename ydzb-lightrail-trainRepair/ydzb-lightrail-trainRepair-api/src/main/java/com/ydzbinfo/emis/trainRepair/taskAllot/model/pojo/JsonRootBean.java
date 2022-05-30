/**
 * Copyright 2021 bejson.com
 */
package com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo;

/**
 * Auto-generated: 2021-03-24 12:19:15
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class JsonRootBean {

    private int Status;
    private int StatusCode;
    private String Message;
    private String Detail;
    private Data Data;

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatusCode(int StatusCode) {
        this.StatusCode = StatusCode;
    }

    public int getStatusCode() {
        return StatusCode;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getMessage() {
        return Message;
    }

    public void setDetail(String Detail) {
        this.Detail = Detail;
    }

    public String getDetail() {
        return Detail;
    }

    public void setData(Data Data) {
        this.Data = Data;
    }

    public Data getData() {
        return Data;
    }

}