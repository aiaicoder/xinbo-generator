package com.xin;
import com.xin.cli.command.GenerateCommand;
import com.xin.cli.CommandExecutor;

import static com.xin.cli.utils.CommandUtils.addArgs;

/**
 * @author xin
 */
public class Main {
    public static void main(String[] args) {
        CommandExecutor commandExecutor = new CommandExecutor();
        if (args.length> 0 && "generate".equals(args[0])){
            args = addArgs(GenerateCommand.class, args);
        }
        commandExecutor.doExecute(args);
    }


}

