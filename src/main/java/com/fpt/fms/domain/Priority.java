package com.fpt.fms.domain;

public enum Priority {
    HIGHTEST("HIGHTEST"),
    HIGH("HIGH"),
    MEDIUM("MEDIUM"),
    LOW("LOW"),
    LOWEST("LOWEST");

    private final String value;

    Priority(String value) {
        this.value = value;
    }

    public static Priority fromString(String text) {
        for (Priority status : Priority.values()) {
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
