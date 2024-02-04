package ${basePackage};
import ${basePackage}.cli.command.GenerateCommand;
import ${basePackage}.cli.CommandExecutor;

import static ${basePackage}.cli.utils.CommandUtils.addArgs;

/**
 * @author ${author}
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

