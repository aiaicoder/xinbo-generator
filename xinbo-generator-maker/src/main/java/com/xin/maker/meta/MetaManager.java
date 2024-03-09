package com.xin.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

/**
 * @author 15712
 */
public class MetaManager {


    private static volatile Meta meta;


    private MetaManager() {
        //私有构造器防止外部实例化
    }


    public static Meta getMetaObject(){
        if (meta == null){
            synchronized (MetaManager.class){
                if (meta == null){
                    meta = initMeta();
                }
            }
        }
        return meta;
    }


    private static Meta initMeta(){
        String metaJson = ResourceUtil.readUtf8Str("springboot-init-meta.json");
        Meta meta = JSONUtil.toBean(metaJson, Meta.class);
        //校验处理默认值,防止用户输入不合法的内容
        MetaValidator.doValidAndFill(meta);
        return meta;

    }

        public static void main(String[] args) {
            Meta meta = MetaManager.getMetaObject();

            System.out.println(meta);
        }
    }

