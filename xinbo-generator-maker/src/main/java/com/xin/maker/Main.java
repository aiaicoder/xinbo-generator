package com.xin.maker;
import com.xin.maker.cli.command.GenerateCommand;
import com.xin.maker.cli.CommandExecutor;

import static com.xin.maker.cli.utils.CommandUtils.addArgs;

/**
 * @author 15712
 */
public class Main {
    public static void main(String[] args) {
        CommandExecutor commandExecutor = new CommandExecutor();
        addArgs(GenerateCommand.class, args);
        commandExecutor.doExecute(args);
    }


}

