package com.lsc.mmzookeeper.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by ValarMorghulis on 2019/2/27 19:25.
 */
public class ZkResult {

    boolean stat;
    List<String> data;
    String msg;
    String time;

    public ZkResult(){
        this.stat=false;
        this.data=null;
        this.msg=null;
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        this.time = dateFormat.format(now);
    }

    public ZkResult(boolean stat, List<String> data, String msg) {
        this.stat = stat;
        this.data = data;
        this.msg = msg;
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        this.time = dateFormat.format(now);
    }

    @Override
    public String toString() {
        return "ZkResult{" +
                "stat=" + stat +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public String getTime() {
        return time;
    }

    public boolean getStat() {
        return stat;
    }

    public void setStat(boolean stat) {
        this.stat = stat;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
