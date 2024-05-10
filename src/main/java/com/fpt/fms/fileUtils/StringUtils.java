package com.fpt.fms.fileUtils;

import java.util.List;

public class StringUtils {

    public static boolean isEmpty(Object object){
        if(object != null){
            if(object instanceof String){
                if(object.toString().trim().equals("")){
                    return true;
                }
            }else if(object instanceof List<?>){
                if(((List) object).size() < 1){
                    return true;
                }
            }

        }
        return true;
    }

}
