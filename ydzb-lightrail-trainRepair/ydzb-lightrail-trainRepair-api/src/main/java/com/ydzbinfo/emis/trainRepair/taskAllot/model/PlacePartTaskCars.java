package com.ydzbinfo.emis.trainRepair.taskAllot.model;

import org.springframework.util.StringUtils;
import sun.swing.StringUIClientPropertyKey;

import java.util.ArrayList;
import java.util.List;

/**
 * 辆序部件前八和后八分析数据
 */
public class PlacePartTaskCars {

    /**
     * 编组情况
     */
    private int marshlType;

    /**
     * 编组辆序顺序(0--全部,1--前八辆,2--后八辆/后九)
     */
    private String distinctCars;

    /**
     * 辆序显示名称
     */
    private String trainsetCar;

    /**
     * 辆序
     */
    private List<String> allCars;

    public void setMarshlType(int marshlType){
        this.marshlType = marshlType;
    }

    public int getMarshlType(){
        return this.marshlType;
    }

    public void setDistinctCars(String distinctCars){
        this.distinctCars = distinctCars;
    }

    public String getDistinctCars(){
        return this.distinctCars;
    }

    public String getTrainsetCar(){
        if(this.trainsetCar == null || this.trainsetCar == ""){
            if(this.marshlType <= 8 || this.distinctCars.equals("0")){
                this.trainsetCar = "全部";
            }
            else if(this.marshlType > 8 && this.distinctCars.equals("1")){
                this.trainsetCar = "前八";
            }
            else if(this.marshlType == 16 && this.distinctCars.equals("2")){
                this.trainsetCar = "后八";
            }
            else if(this.marshlType == 17 && this.distinctCars.equals("2")){
                this.trainsetCar = "后九";
            }
        }
        return  this.trainsetCar;
    }

    public List<String> getAllCars(){
        if(this.allCars == null || this.allCars.size() == 0){
            allCars = new ArrayList<String>();
            //4编组、6编组和8编组
            if(marshlType <= 8){
                for(int i = 1;i < marshlType; i++){
                    allCars.add("0"+i);
                }
                allCars.add("00");
            }
            //16编组和17编组
            else if(marshlType > 8){
                if(distinctCars.equals("1")){
                    for(int i = 1;i < 9; i++){
                        allCars.add("0"+i);
                    }
                }else if(distinctCars.equals("2")){
                    for(int i= 9; i < marshlType;i++){
                        String left = i <10 ? "0":"";
                        allCars.add(left+i);
                    }
                    allCars.add("00");
                }
            }
        }
        return this.allCars;
    }
}
