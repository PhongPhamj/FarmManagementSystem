package com.fpt.fms.domain;

public enum ApproveStatus {
    APPROVED("APPROVED"),
    REJECT("REJECT"),
    REQUEST("REQUEST");

    private final String value;

    ApproveStatus(String value) {
        this.value = value;
    }

    public static ApproveStatus fromString(String text) {
        for (ApproveStatus status : ApproveStatus.values()) {
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
