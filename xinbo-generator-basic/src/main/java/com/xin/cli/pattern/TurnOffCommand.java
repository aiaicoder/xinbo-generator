package com.xin.cli.pattern;

import lombok.Data;

/**
 * @author 15712
 */

public class TurnOffCommand implements Command {

    private Device device;

    public TurnOffCommand(Device device) {
        this.device = device;
    }
    @Override
    public void execute() {
        device.turnOff();
    }
}
