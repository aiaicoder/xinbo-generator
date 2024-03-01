package com.xin.maker.meta.enums;

/**
 * @author 15712
 */

public enum FileGenerateTypeEnum {
    STATIC("静态","static"),
    DYNAMIC("动态","dynamic");

    private final String text;
    private final String value;

    FileGenerateTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getName() {
        return text;
    }

    public String getValue() {
        return value;
    }


}
