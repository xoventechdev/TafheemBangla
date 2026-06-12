package com.minbar.tafhimulquran.Prayer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    private static final String[] BENGALI_DIGITS = {"০", "১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯"};
    private static final String[] BENGALI_MONTHS = {
            "মুহাররম", "সফর", "রবিউল আউয়াল", "রবিউস সানি", "জুমাদাল আউয়াল", "জুমাদাস সানি",
            "রজব", "শাবান", "রমজান", "শাওয়াল", "জিলকদ", "জিলহজ্জ"
    };

    public static String formatHijriDate(String hijriDate, int monthNumber) {
        if (hijriDate == null || hijriDate.isEmpty()) return "";
        String[] parts = hijriDate.split("-");
        if (parts.length != 3) return hijriDate;

        try {
            String day = convertToBengaliDigits(parts[0]);
            String month = (monthNumber >= 1 && monthNumber <= 12) ? BENGALI_MONTHS[monthNumber - 1] : "";
            String year = convertToBengaliDigits(parts[2]);
            return day + " " + month + " " + year;
        } catch (Exception e) {
            return hijriDate;
        }
    }

    /**
     * Converts Western digits (0-9) in a string to Bengali digits.
     * Safe against non-Western digits and already converted digits.
     */
    public static String convertToBengaliDigits(String number) {
        if (number == null || number.isEmpty()) return "";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < number.length(); i++) {
            char c = number.charAt(i);
            if (c >= '0' && c <= '9') {
                result.append(BENGALI_DIGITS[c - '0']);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static String formatTimeTo12Hour(String time24Hour) {
        if (time24Hour == null || time24Hour.isEmpty()) return "--:--";
        try {
            String cleanTime = time24Hour.split(" ")[0];
            SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm", Locale.US);
            Date date = sdf24.parse(cleanTime);
            SimpleDateFormat sdf12 = new SimpleDateFormat("hh:mm a", Locale.US);
            return sdf12.format(date).replace("AM", "এএম").replace("PM", "পিএম");
        } catch (Exception e) {
            return time24Hour;
        }
    }

    public static String getNextPrayer(PrayerTimesResponse.Data today) {
        return getNextPrayer(today, null);
    }

    public static String getNextPrayer(PrayerTimesResponse.Data today, PrayerTimesResponse.Data tomorrow) {
        if (today == null || today.timings == null) return "অজানা|--:--";

        String timezone = "Asia/Dhaka"; // Default timezone
        if (today.meta != null && today.meta.timezone != null) {
            timezone = today.meta.timezone;
        }
        TimeZone tz = TimeZone.getTimeZone(timezone);
        Calendar now = Calendar.getInstance(tz);
        int currentTimeInMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);

        PrayerTimesResponse.Data.Timings todayTimings = today.timings;
        // Milestones: Fajr, Sunrise, Dhuhr, Asr, Maghrib, Isha
        String[] milestoneTimes = {todayTimings.Fajr, todayTimings.Sunrise, todayTimings.Dhuhr, todayTimings.Asr, todayTimings.Maghrib, todayTimings.Isha};
        String[] milestoneNames = {"ফজর", "সূর্যোদয়", "যোহর", "আসর", "মাগরিব", "ইশা"};
        
        for (int i = 0; i < milestoneTimes.length; i++) {
            int mMin = getMinutesFromTime(milestoneTimes[i]);
            if (mMin != -1 && currentTimeInMinutes < mMin) {
                return milestoneNames[i] + "|" + formatTimeTo12Hour(milestoneTimes[i]);
            }
        }

        String nextFajr = (tomorrow != null && tomorrow.timings != null && tomorrow.timings.Fajr != null) 
                ? tomorrow.timings.Fajr : todayTimings.Fajr;
        return "ফজর (আগামীকাল)|" + formatTimeTo12Hour(nextFajr);
    }

    private static int getMinutesFromTime(String time) {
        if (time == null || !time.contains(":")) return -1;
        try {
            String cleanTime = time.split(" ")[0];
            String[] parts = cleanTime.split(":");
            return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        } catch (Exception e) {
            return -1;
        }
    }

    public static long[] getElapsedAndTotal(PrayerTimesResponse.Data today) {
        return getElapsedAndTotal(today, null);
    }

    public static long[] getElapsedAndTotal(PrayerTimesResponse.Data today, PrayerTimesResponse.Data tomorrow) {
        if (today == null || today.timings == null) return new long[]{0, 0};

        try {
            String timezone = "Asia/Dhaka"; // Default timezone
            if (today.meta != null && today.meta.timezone != null) {
                timezone = today.meta.timezone;
            }
            TimeZone tz = TimeZone.getTimeZone(timezone);
            Calendar now = Calendar.getInstance(tz);
            long currentTime = now.getTimeInMillis();

            PrayerTimesResponse.Data.Timings todayTimings = today.timings;
            String[] prayerTimes = {todayTimings.Fajr, todayTimings.Sunrise, todayTimings.Dhuhr, todayTimings.Asr, todayTimings.Maghrib, todayTimings.Isha};
            long[] prayerMillis = new long[6];

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
            sdf.setTimeZone(tz);

            for (int i = 0; i < prayerTimes.length; i++) {
                if (prayerTimes[i] == null) continue;
                String cleanTime = prayerTimes[i].split(" ")[0];
                Date date = sdf.parse(cleanTime);
                Calendar temp = Calendar.getInstance(tz);
                temp.setTime(date);

                Calendar cal = (Calendar) now.clone();
                cal.set(Calendar.HOUR_OF_DAY, temp.get(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, temp.get(Calendar.MINUTE));
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                prayerMillis[i] = cal.getTimeInMillis();
            }

            long start = 0, end = 0;

            if (currentTime < prayerMillis[0]) {
                start = prayerMillis[5] - 24 * 60 * 60 * 1000;
                end = prayerMillis[0];
            } else if (currentTime >= prayerMillis[5]) {
                start = prayerMillis[5];
                if (tomorrow != null && tomorrow.timings != null && tomorrow.timings.Fajr != null) {
                    end = getMillisForTimeTomorrow(tomorrow.timings.Fajr, timezone);
                } else {
                    end = prayerMillis[0] + 24 * 60 * 60 * 1000;
                }
            } else {
                for (int i = 0; i < prayerMillis.length - 1; i++) {
                    if (currentTime >= prayerMillis[i] && currentTime < prayerMillis[i + 1]) {
                        start = prayerMillis[i];
                        end = prayerMillis[i + 1];
                        break;
                    }
                }
            }

            if (end > start) {
                return new long[]{currentTime - start, end - start};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new long[]{0, 0};
    }

    private static long getMillisForTimeTomorrow(String time, String timezone) {
        try {
            String cleanTime = time.split(" ")[0];
            String[] parts = cleanTime.split(":");
            TimeZone tz = TimeZone.getTimeZone(timezone);
            Calendar cal = Calendar.getInstance(tz);
            cal.add(Calendar.DATE, 1);
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTimeInMillis();
        } catch (Exception e) {
            return System.currentTimeMillis() + 24 * 60 * 60 * 1000;
        }
    }
}
