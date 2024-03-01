package com.xin.maker.template.enums;

import cn.hutool.core.util.ObjectUtil;
import com.xin.maker.meta.enums.FileGenerateTypeEnum;
import lombok.Getter;

/**
 * @author 15712
 */
@Getter
public enum FileFilterRangeEnum {
    FILE_NAME("文件名称", "fileName"),
    FILE_CONTENT("文件内容", "fileContent");

    private final String text;

    private final String value;

    FileFilterRangeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static FileFilterRangeEnum getEnumByValue(String value){
        if (ObjectUtil.isNull(value)){
            return null;
        }
        for (FileFilterRangeEnum anEnum : FileFilterRangeEnum.values()) {
            if (anEnum .value.equals(value)){
                return anEnum;
            }
        }
        return null;
    }
}
