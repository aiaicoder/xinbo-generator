package com.xin;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ArrayUtil;
import com.xin.cli.CommandExecutor;
import com.xin.cli.command.GenerateCommand;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static com.xin.cli.utils.CommandUtils.addArgs;
import static com.xin.generator.StaticGenerator.copyFilesByRecursive;

/**
 * @author 15712
 */
public class Main {
    public static void main(String[] args) {
        args = new String[]{"generate","-a","lalal","-l","false"};
//        args = new String[]{"list"};
        CommandExecutor commandExecutor = new CommandExecutor();
        args = addArgs(GenerateCommand.class, args);
        commandExecutor.doExecute(args);
    }


}

