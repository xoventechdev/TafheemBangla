package com.minbar.tafhimulquran.Prayer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateUtils {
    // Bengali digits mapping
    private static final String[] BENGALI_DIGITS = {"০", "১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯"};
    private static final String[] BENGALI_MONTHS = {
            "মুহাররম", "সফর", "রবিউল আউয়াল", "রবিউস সানি", "জুমাদাল আউয়াল", "জুমাদাস সানি",
            "রজব", "শাবান", "রমজান", "শাওয়াল", "জিলকদ", "জিলহজ্জ"
    };
    private static final String[] PRAYER_NAMES = {"ফজর", "যোহর", "আসর", "মাগরিব", "ইশা"};

    public static String formatHijriDate(String hijriDate, int monthNumber) {
        // Input format: DD-MM-YYYY
        String[] parts = hijriDate.split("-");
        if (parts.length != 3) return hijriDate;

        String day = convertToBengaliDigits(parts[0]);
        String month = BENGALI_MONTHS[monthNumber - 1]; // Use monthNumber from Month object
        String year = convertToBengaliDigits(parts[2]);

        return day + " " + month + " " + year;
    }

    public static String convertToBengaliDigits(String number) {
        StringBuilder result = new StringBuilder();
        for (char c : number.toCharArray()) {
            if (Character.isDigit(c)) {
                result.append(BENGALI_DIGITS[c - '0']);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static String formatTimeTo12Hour(String time24Hour) {
        try {
            SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm", Locale.US);
            Date date = sdf24.parse(time24Hour);
            SimpleDateFormat sdf12 = new SimpleDateFormat("hh:mm a", Locale.US);
            return sdf12.format(date);
        } catch (ParseException e) {
            return time24Hour;
        }
    }

    public static String getNextPrayer(PrayerTimesResponse.Data.Timings timings) {
        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);
        int currentTimeInMinutes = currentHour * 60 + currentMinute;

        String[] prayerTimes = {timings.Fajr, timings.Dhuhr, timings.Asr, timings.Maghrib, timings.Isha};
        int[] prayerMinutes = new int[5];

        for (int i = 0; i < prayerTimes.length; i++) {
            String[] timeParts = prayerTimes[i].split(":");
            prayerMinutes[i] = Integer.parseInt(timeParts[0]) * 60 + Integer.parseInt(timeParts[1]);
        }

        for (int i = 0; i < prayerMinutes.length; i++) {
            if (currentTimeInMinutes < prayerMinutes[i]) {
                String endTime = (i < prayerMinutes.length - 1) ? prayerTimes[i + 1] : "11:59 PM";
                return PRAYER_NAMES[i] + " " + formatTimeTo12Hour(prayerTimes[i]) + " - " + formatTimeTo12Hour(endTime);
            }
        }

        // If all prayers are done for the day, return Fajr for the next day
        return PRAYER_NAMES[0] + " " + formatTimeTo12Hour(timings.Fajr) + " - " + formatTimeTo12Hour(timings.Dhuhr);
    }

    public static String[] getRestrictedTimes(String sunrise, String dhuhr, String maghrib) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
            Date sunriseDate = sdf.parse(sunrise);
            Date dhuhrDate = sdf.parse(dhuhr);
            Date maghribDate = sdf.parse(maghrib);

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf12 = new SimpleDateFormat("hh:mm a", Locale.US);

            // Sunrise restricted time: 15 minutes before and after
            cal.setTime(sunriseDate);
            cal.add(Calendar.MINUTE, -15);
            String sunriseStart = sdf12.format(cal.getTime());
            cal.add(Calendar.MINUTE, 30);
            String sunriseEnd = sdf12.format(cal.getTime());

            // Midday restricted time: 5 minutes before and after Dhuhr
            cal.setTime(dhuhrDate);
            cal.add(Calendar.MINUTE, -5);
            String middayStart = sdf12.format(cal.getTime());
            cal.add(Calendar.MINUTE, 10);
            String middayEnd = sdf12.format(cal.getTime());

            // Sunset restricted time: 15 minutes before and after Maghrib
            cal.setTime(maghribDate);
            cal.add(Calendar.MINUTE, -15);
            String sunsetStart = sdf12.format(cal.getTime());
            cal.add(Calendar.MINUTE, 30);
            String sunsetEnd = sdf12.format(cal.getTime());

            return new String[]{
                    "নিষিদ্ধ সময় (সকাল): " + sunriseStart + " - " + sunriseEnd,
                    "নিষিদ্ধ সময় (দুপুর): " + middayStart + " - " + middayEnd,
                    "নিষিদ্ধ সময় (সন্ধ্যা): " + sunsetStart + " - " + sunsetEnd
            };
        } catch (ParseException e) {
            return new String[]{"Error calculating restricted times"};
        }
    }
}