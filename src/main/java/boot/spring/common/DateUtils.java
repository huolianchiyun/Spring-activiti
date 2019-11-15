package boot.spring.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {
    private final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    /**
     * @descript:yyyy-MM-dd hh:mm:ss
     * @return: a date string
     * @auther: zhangbin
     * @date: 2019/11/14 19:36
     */
    public static String getCurrentDate(){
        return format.format(new Date());
    }
}
