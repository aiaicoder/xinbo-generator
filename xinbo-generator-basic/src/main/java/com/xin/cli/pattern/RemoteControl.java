package com.xin.cli.pattern;

/**
 * @author 15712
 */
public class RemoteControl {

    private Command command;

    public RemoteControl(Command command) {
        this.command = command;
    }

    public void execute() {
        command.execute();
    }
}
