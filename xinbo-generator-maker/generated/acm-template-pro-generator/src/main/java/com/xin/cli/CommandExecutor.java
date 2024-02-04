package com.xin.cli;


import com.xin.cli.command.ConfigCommand;
import com.xin.cli.command.GenerateCommand;
import com.xin.cli.command.ListCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * @author xin
 */
@Command(name = "xinbo",mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable{

    private final CommandLine commandLine;

    {
        commandLine = new CommandLine(this).
                addSubcommand(new ListCommand()).
                addSubcommand(new GenerateCommand()).
                addSubcommand(new ConfigCommand());
    }


    @Override
    public void run() {
        //不输入命令的时候给出提示
        System.out.println("请输入具体指令，或者输入--help查看帮助手册");
    }

    /**
     * 执行命令
     * @param args
     */
    public Integer doExecute(String[] args) {
        return commandLine.execute(args);
    }


}
