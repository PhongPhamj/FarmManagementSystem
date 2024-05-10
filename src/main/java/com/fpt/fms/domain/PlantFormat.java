package com.fpt.fms.domain;

public enum PlantFormat {
    BED("BED"),//Luống
    PLOT("PLOT"),//Thửa
    HILL("HILL"),//Đồi
    OTHER("OTHER");//Khác
    private final String value;

    PlantFormat(String value) {
        this.value = value;
    }

    public static PlantFormat fromString(String text) {
        for (PlantFormat status : PlantFormat.values()) {
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
