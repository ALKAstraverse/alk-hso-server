package core;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class Util {

    private static final Random random = new Random();
    public static final SimpleDateFormat fmt_save_log = new SimpleDateFormat("dd_MM_yyyy");
    private static final SimpleDateFormat fmt_get_time_now = new SimpleDateFormat("hh:mm:ss a");
    private static final SimpleDateFormat fmt_is_same_day = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
    private static final NumberFormat en = NumberFormat.getInstance(new Locale("vi"));

    static {
        TimeZone vnTimeZone = TimeZone.getTimeZone("GMT+7");
        fmt_save_log.setTimeZone(vnTimeZone);
        fmt_get_time_now.setTimeZone(vnTimeZone);
        fmt_is_same_day.setTimeZone(vnTimeZone);
        sdf.setTimeZone(vnTimeZone);
    }

    public synchronized static byte[] loadfile(String url) throws IOException {
        try ( FileInputStream fis = new FileInputStream(url)) {
            byte[] ab = new byte[fis.available()];
            fis.read(ab, 0, ab.length);
            return ab;
        }
    }

    public static int log_2(int a) {
        int i;
        for (i = 0; i < a; i++) {
            if (Math.pow(2, i) >= a) {
                break;
            }
        }
        return i;
    }

    public static void logconsole(String s, int type, byte cmd) {
        if (Manager.gI().debug && cmd != 4 && cmd != 5 && cmd != 7 && cmd != -52) {
            switch (type) {
                case 0 -> System.out.println(s);
                case 1 -> System.err.println(s);
            }
        }
    }

    public static int random(int a1, int a2) {
        return random.nextInt(a1, a2);
    }

    public static int random(int a2) {
        return random.nextInt(0, a2);
    }

    public static String get_now_by_time() {
        return fmt_get_time_now.format(new Date());
    }

    public synchronized static Date getDate(String day) {
        try {
            return sdf.parse(day);
        } catch (ParseException e) {
            return Date.from(Instant.now());
        }
    }

    public static boolean is_same_day(Date date1, Date date2) {
        return fmt_is_same_day.format(date1).equals(fmt_is_same_day.format(date2));
    }

    public static String getTime(int n) {
        int h = n / 3600;
        int p = (n % 3600) / 60;
        int s = n % 60;
        return String.format("%dh:%dp:%ds", h, p, s);
    }

    public static boolean isnumber(String txt) {
        try {
            Integer.parseInt(txt);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String number_format(long num) {
        return en.format(num);
    }
}