package com.handheld.huang.handsettest.adapter;

/**
 *
 * @author huang
 * @date 2017/11/20
 */

public class Result {
    String name;
    int isCheck;

    public Result(){

    }

    public Result(String name, int isCheck) {
        this.name = name;
        this.isCheck = isCheck;
    }

    public String getName() {
        return name;
    }

    public int getIsCheck() {
        return isCheck;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }
}
