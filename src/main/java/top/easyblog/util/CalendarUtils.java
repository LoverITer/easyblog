package top.easyblog.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * @author huangxin
 */
public class CalendarUtils {


    volatile private static CalendarUtils calendarUtils = null;
    private static final ThreadLocal<SimpleDateFormat> threadLocal=new ThreadLocal<>();
    private final Object lock=new Object();

    private CalendarUtils() {

    }

    //DCL双检查锁
    public static CalendarUtils getInstance() {
        if (Objects.isNull(calendarUtils)) {
            synchronized (CalendarUtils.class) {
                if (Objects.isNull(calendarUtils)) {
                    calendarUtils = new CalendarUtils();
                }
            }
        }
        return calendarUtils;
    }

    /**
     * 根据一个日期串获得Date实例，支持的日期串形式:
     *  YYYY-MM-DD
     *  YYYY/MM/DD
     *  YYYY_MM_DD
     *  YYYYMMDD
     *  YYYY.MM.DD
     * @param dateStr
     * @return
     */
    public Date getDate(String dateStr){
        Calendar calendar=Calendar.getInstance();
        if(RegexUtils.isDate(dateStr)){
            int year;
            int month;
            int day;
            if(dateStr.length()==8){
                year=Integer.parseInt(dateStr.substring(0, 4));
                month=Integer.parseInt(dateStr.substring(4, 6));
                day=Integer.parseInt(dateStr.substring(6, 8));
            }else{
                year=Integer.parseInt(dateStr.substring(0, 4));
                month=Integer.parseInt(dateStr.substring(5, 7));
                day=Integer.parseInt(dateStr.substring(8, 10));
            }
            calendar.clear();
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,month-1);
            calendar.set(Calendar.DAY_OF_MONTH,day);
        }
        return calendar.getTime();
    }

    /**
     * 得到线程安全的SimpleDateFormat实例
     * @param pattern
     * @return
     */
    public SimpleDateFormat getSafeSimpleDateFormat(String pattern) {
        SimpleDateFormat sdf=threadLocal.get();
        if(sdf==null){
            synchronized (lock){
                if(sdf==null){
                    sdf=new SimpleDateFormat(pattern);
                    threadLocal.set(sdf);
                }
            }
        }
        return sdf;
    }

    /**
     * 两个时间相差距离：x年x月x日x天x小时x分x秒
     *
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return long[] 返回值为：{年,月,天, 时, 分, 秒}
     */
    public static long[] getDateDistanceTimes(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one = null, two = null;
        long diff = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            diff = Math.abs(time1 - time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long day = diff / (24 * 60 * 60 * 1000);
        long year = day / 365;
        long month = day / 30;
        long hour = diff / (60 * 60 * 1000) - day * 24;
        long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

        return new long[]{year, month, day, hour, min, sec};
    }

    /**
     * 计算
     *
     * @param date
     * @return
     */
    public static String getDateDistanceInfo(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long[] times = getDateDistanceTimes(sdf.format(date), sdf.format(new Date()));
        StringBuilder dateInfo = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        if (times.length == 0) {
            return "";
        }
        if (times[0] > 0) {
            dateInfo.append(year).append("-").append(paddingZeroPrefix(String.valueOf(month))).append("-").append(paddingZeroPrefix(String.valueOf(day)));
        } else if (times[1] >=1) {
            dateInfo.append(paddingZeroPrefix(String.valueOf(month))).append("-").append(paddingZeroPrefix(String.valueOf(day)));
        } else if (times[2] >= 7) {
            dateInfo.append(times[2] / 7).append("周前");
        } else if (times[2] > 0) {
            if (times[2] == 1) {
                dateInfo.append("昨天 ").append(paddingZeroPrefix(String.valueOf(hour))).append(":").append(paddingZeroPrefix(String.valueOf(min)));
            } else if (times[2] == 2) {
                dateInfo.append("前天 ").append(paddingZeroPrefix(String.valueOf(hour))).append(":").append(paddingZeroPrefix(String.valueOf(min)));
            } else {
                dateInfo.append(times[2]).append("天前");
            }
        } else if (times[3] != 0) {
            dateInfo.append(times[3]).append("小时前");
        } else if (times[4] != 0) {
            dateInfo.append(times[4]).append("分钟前");
        } else {
            dateInfo.append(times[5]).append("秒钟前");
        }
        return dateInfo.toString();
    }

    /**
     * 把数字格式化为两位数的格式
     *
     * @param str
     * @return
     */
    private static String paddingZeroPrefix(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(str);
        if (str.length() < 2) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }

}
