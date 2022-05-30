/**
  * Copyright 2021 bejson.com 
  */
package com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * Auto-generated: 2021-03-24 12:19:15
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccessToken implements Serializable {

    private Header header;
    private Payload payload;
    private String signature;
    public void setHeader(Header header) {
         this.header = header;
     }
     public Header getHeader() {
         return header;
     }

    public void setPayload(Payload payload) {
         this.payload = payload;
     }
     public Payload getPayload() {
         return payload;
     }

    public void setSignature(String signature) {
         this.signature = signature;
     }
     public String getSignature() {
         return signature;
     }

}