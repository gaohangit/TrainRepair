/**
  * Copyright 2021 bejson.com 
  */
package com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Auto-generated: 2021-03-24 12:19:15
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payload {

    private String iss;
    private String sub;
    private String aud;
    private long exp;
    private long iat;
    private String jti;
    private String unitCode;
    private String unitName;
    private List<String> urlList;
    public void setIss(String iss) {
         this.iss = iss;
     }
     public String getIss() {
         return iss;
     }

    public void setSub(String sub) {
         this.sub = sub;
     }
     public String getSub() {
         return sub;
     }

    public void setAud(String aud) {
         this.aud = aud;
     }
     public String getAud() {
         return aud;
     }

    public void setExp(long exp) {
         this.exp = exp;
     }
     public long getExp() {
         return exp;
     }

    public void setIat(long iat) {
         this.iat = iat;
     }
     public long getIat() {
         return iat;
     }

    public void setJti(String jti) {
         this.jti = jti;
     }
     public String getJti() {
         return jti;
     }

    public void setUnitCode(String unitCode) {
         this.unitCode = unitCode;
     }
     public String getUnitCode() {
         return unitCode;
     }

    public void setUnitName(String unitName) {
         this.unitName = unitName;
     }
     public String getUnitName() {
         return unitName;
     }

    public List<String> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<String> urlList) {
        this.urlList = urlList;
    }
}