package com.quizchii.Enum;

public enum SortDir {
    ASC("ASC"), DESC ("DESC");
    private final String value;

    private SortDir(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
