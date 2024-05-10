package com.fpt.fms.domain;

public enum StatusProcess {
    TODO("TODO"),
    DONE("DONE"),
    MISSED("MISSED"),
    SKIPPED("SKIPPED");

    private final String value;

    StatusProcess(String value) {
        this.value = value;
    }

    public static StatusProcess fromString(String text) {
        for (StatusProcess status : StatusProcess.values()) {
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
