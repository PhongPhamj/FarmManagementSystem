package com.fpt.fms.domain;

public enum RepeatStatus {
    NOTREPEAT("NOTREPEAT"),
    DAILY("DAILY"),
    WEEKLY("WEEKLY"),
    MONTHLY("MONTHLY");

    private final String value;

    RepeatStatus(String value) {
        this.value = value;
    }

    public static RepeatStatus fromString(String text) {
        for (RepeatStatus status : RepeatStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }
}
