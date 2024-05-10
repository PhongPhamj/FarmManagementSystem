package com.fpt.fms.domain;

public enum FarmRole {
    EMPLOYEE("EMPLOYEE"),
    MANAGER("MANAGER"),
    OWNER("OWNER");

    private final String value;

    FarmRole(String value) {
        this.value = value;
    }

    public static FarmRole fromString(String text) {
        for (FarmRole status : FarmRole.values()) {
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
