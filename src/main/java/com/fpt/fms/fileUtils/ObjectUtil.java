package com.fpt.fms.fileUtils;

import com.fpt.fms.config.Constants;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.function.Supplier;


public class ObjectUtil {
    private ObjectUtil() {}

    /**
     * This method to wrap NullPointerException (NPE)
     *
     * @param <T>
     * @param supplier
     * @return <b>the object</b> if not has NPE<br>
     * <b>null</b> if has NPE
     */
    public static <T> T wrapNullPointer(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static <T> T wrapNullPointer(Supplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (NullPointerException e) {
            return defaultValue;
        }
    }

    public static BigDecimal toZeroWithNull(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value;
    }

    public static String toFullName(String firstName, String lastName) {
        return String.format(Constants.FULL_NAME_FORMAT, toBlankWithNull(firstName), toBlankWithNull(lastName));
    }

    public static String toBlankWithNull(String value) {
        if (value == null) {
            return StringUtils.EMPTY;
        }
        return value;
    }

    public static String getUsername(String email) {
        String[] splitEmailArr = email.split("@");
        return splitEmailArr[0];
    }

}
