package com.xin.cli.command;

import cn.hutool.core.util.ReflectUtil;

import com.xin.model.DataModel;
import lombok.Data;
import picocli.CommandLine.*;

import java.lang.reflect.Field;

/**
 * @author xin
 * 查看配置参数信息
 */
@Command(name = "config", mixinStandardHelpOptions = true,description = "查看参数信息")
@Data
public class ConfigCommand implements Runnable {
    @Override
    public void run() {
        //ConfigCommand
        System.out.println("查看参数信息");
        //获得参数字段
        Field[] fields = ReflectUtil.getFields(DataModel.class);
        for (Field field : fields) {
            System.out.println("字段名称："+field.getName());
            System.out.println("字段类型："+field.getType());
            System.out.println("------");
        }
    }
}
