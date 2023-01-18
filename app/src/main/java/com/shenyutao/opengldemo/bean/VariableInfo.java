package com.shenyutao.opengldemo.bean;

public class VariableInfo {
    private String modifier;
    private String type;
    private String name;

    private int handle;

    public VariableInfo(String modifier, String type, String name) {
        this.modifier = modifier;
        this.type = type;
        this.name = name;
    }


    public int getHandle() {
        return handle;
    }

    public void setHandle(int handle) {
        this.handle = handle;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "VariableInfo{" +
                "modifier='" + modifier + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", handle=" + handle +
                '}';
    }


    public static final String UNIFORM = "uniform";
    public static final String ATTRIBUTE = "attribute";


}
