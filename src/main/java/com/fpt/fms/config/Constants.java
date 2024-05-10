package com.fpt.fms.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int PAGE_SIZE_DEFAULT = 10;
    public static final String FULL_NAME_FORMAT = "%s %s";
    public static final String SYMBOL_COMMA = ",";

    private Constants() {}
}
