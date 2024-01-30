package com.xin.cli.example;


import com.sun.org.apache.bcel.internal.generic.NEW;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

/**
 * @author 15712
 */
@Command(name = "Main",mixinStandardHelpOptions = true)
public class SubCommandExample implements Runnable {

    @Override
    public void run() {
        System.out.println("这是主要操作");
    }
    @Command(name = "Add",mixinStandardHelpOptions = true,description = "这是添加操作")
    static class Add implements Runnable{
        @Override
        public void run() {
            System.out.println("正在添加");
        }
    }

    @Command(name = "Delete",mixinStandardHelpOptions = true,description = "这是删除操作")
    static class Delete implements Runnable{
        @Override
        public void run() {
            System.out.println("正在删除");
        }
    }

    @Command(name = "Update",mixinStandardHelpOptions = true,description = "这是更新操作")
    static class Update implements Runnable{
        @Override
        public void run() {
            System.out.println("正在更新");
        }
    }

    public static void main(String[] args) {
        //这是全局帮助指令，会提示子命令的使用方式
//        args = new String[] {"-help"};
//          args = new String[] {"Add"};
        //直接提示对应子命令的使用方式
//          args = new String[] {"Add","-h"};
        //执行不存在的命令会报错
        args = new String[] {"Uddate"};
        new CommandLine(new SubCommandExample()).
                addSubcommand(new Add()).
                addSubcommand(new Delete()).
                addSubcommand(new Update())
                .execute(args);
    }
}
