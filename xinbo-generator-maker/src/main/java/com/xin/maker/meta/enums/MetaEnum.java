package com.xin.maker.meta.enums;

public enum MetaEnum {
    FILE("文件","file"),
    DIR("目录","dir"),
    GROUP("文件组", "group");

    private final String text;
    private final String value;

    MetaEnum(String text, String value) {
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
