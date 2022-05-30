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
public class RefreshToken {

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