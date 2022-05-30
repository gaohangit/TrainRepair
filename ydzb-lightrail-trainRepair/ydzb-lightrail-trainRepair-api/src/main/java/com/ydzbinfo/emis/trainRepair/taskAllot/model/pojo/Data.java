/**
  * Copyright 2021 bejson.com 
  */
package com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo;

import java.io.Serializable;

/**
 * Auto-generated: 2021-03-24 12:19:15
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Data implements Serializable {

    private AccessToken AccessToken;
    private RefreshToken RefreshToken;
    public void setAccessToken(AccessToken AccessToken) {
         this.AccessToken = AccessToken;
     }
     public AccessToken getAccessToken() {
         return AccessToken;
     }

    public void setRefreshToken(RefreshToken RefreshToken) {
         this.RefreshToken = RefreshToken;
     }
     public RefreshToken getRefreshToken() {
         return RefreshToken;
     }

    @Override
    public String toString() {
        return "Data{" +
                "AccessToken=" + AccessToken +
                ", RefreshToken=" + RefreshToken +
                '}';
    }
}