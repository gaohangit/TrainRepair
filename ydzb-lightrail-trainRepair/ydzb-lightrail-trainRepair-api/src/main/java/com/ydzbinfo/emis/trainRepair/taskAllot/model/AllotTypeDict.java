package com.ydzbinfo.emis.trainRepair.taskAllot.model;

public class AllotTypeDict {

    /**
     * 派工状态code
     */
    private String code;

    /**
     * 派工状态name
     */
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AllotTypeDict{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
