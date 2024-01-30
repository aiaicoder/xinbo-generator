package com.xin.cli.pattern;

/**
 * @author 15712
 */
public class Client {

    public static void main(String[] args) {
        Device tv = new Device("TV");
        //拿到遥控器，即命令的调用者
        RemoteControl turnOn = new RemoteControl(new TurnOnCommand(tv));
        RemoteControl turnOff = new RemoteControl(new TurnOffCommand(tv));
        turnOn.execute();
        turnOff.execute();
    }
}
