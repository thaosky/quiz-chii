package com.quizchii.Enum;

/**
 * ONCE_WITH_TIME => Thi 1 lần time cố định
 * ONCE_WITHOUT_TIME => Thi 1 lần lúc nào cũng đc
 * PRACTICE => Freestyle
 */
public enum TestType {
    ONCE_WITH_TIME("ONCE_WITH_TIME"), ONCE_WITHOUT_TIME("ONCE_WITHOUT_TIME"), PRACTICE("PRACTICE");
    private String value;

    TestType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
