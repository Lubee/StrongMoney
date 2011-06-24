package strong.com.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

  private static SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");

  public static String getCurrentTime() {
     return sp.format(new Date());
   // Calendar c = Calendar.getInstance();
   //return  new StringBuilder(String.valueOf(c.get(Calendar.YEAR))).append("-").append(c.get(Calendar.MONTH) + 1).append("-").append(c.get(Calendar.DAY_OF_MONTH)).toString();
  }

  public static Calendar getStringToCalender(String dateStr) {
    Calendar c = Calendar.getInstance();
    try {
      Date date = sp.parse(dateStr);
      c.setTime(date);
    } catch (ParseException e) {
    }
    return c;
  }

  public static int getCurrentYear() {
    Calendar c = Calendar.getInstance();
    return c.get(Calendar.YEAR);
  }
}
