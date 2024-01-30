package com.xin.cli.pattern;

/**
 * @author 15712
 */
public class Device {

    private String name;

    public Device(String name) {
        this.name = name;
    }

    public void turnOn()
    {
        System.out.println("设备 " + name + " 开始启动");
    }
    public void turnOff()
    {
        System.out.println("设备 " + name + " 关闭");
    }
}
