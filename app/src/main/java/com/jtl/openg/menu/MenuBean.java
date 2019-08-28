package com.jtl.openg.menu;

import androidx.annotation.NonNull;

/**
 * 作者:jtl
 * 日期:Created in 2019/8/27 11:18
 * 描述:
 * 更改:
 */
public class MenuBean {
    private String name;
    private Class mClassData;

    public MenuBean(String name) {
        this.name = name;
    }

    public MenuBean(String name, Class classData) {
        this.name = name;
        mClassData = classData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClassData(Class classData) {
        mClassData = classData;
    }

    public Class getClassData() {
        return mClassData;
    }
}
