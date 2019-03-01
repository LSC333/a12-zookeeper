package com.lsc.mmserver.entity;

/**
 * Created by ValarMorghulis on 2019/3/1 16:43.
 */
public class ServerStatus {     //服务器的利用状态

    double cpu;  //cpu利用率, 范围[0,1]
    double ram;  //内存利用率, 范围[0,1]

    public ServerStatus(){
        this.cpu=0.0;
        this.ram=0.0;
    }

    public ServerStatus(double cpu, double ram){
        this.cpu=cpu;
        this.ram=ram;
    }

    @Override
    public String toString() {
        return "ServerStatus{" +
                "cpu=" + cpu +
                ", ram=" + ram +
                '}';
    }

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public double getRam() {
        return ram;
    }

    public void setRam(double ram) {
        this.ram = ram;
    }

}
