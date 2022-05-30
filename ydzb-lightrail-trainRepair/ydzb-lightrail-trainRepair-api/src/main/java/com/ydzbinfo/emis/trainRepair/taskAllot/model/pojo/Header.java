/**
  * Copyright 2021 bejson.com 
  */
package com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Auto-generated: 2021-03-24 12:19:15
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Header {

    private String alg;
    private String typ;
    public void setAlg(String alg) {
         this.alg = alg;
     }
     public String getAlg() {
         return alg;
     }

    public void setTyp(String typ) {
         this.typ = typ;
     }
     public String getTyp() {
         return typ;
     }

}