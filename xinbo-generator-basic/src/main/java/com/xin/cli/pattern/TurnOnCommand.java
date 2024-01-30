package com.xin.cli.pattern;

import lombok.Data;

/**
 * @author 15712
 */

public class TurnOnCommand implements Command{
    private Device device;

    public TurnOnCommand(Device device) {
        this.device = device;
    }
    @Override
    public void execute() {
        device.turnOn();
    }
}
