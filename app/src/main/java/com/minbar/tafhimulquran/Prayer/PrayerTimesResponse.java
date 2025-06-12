package com.minbar.tafhimulquran.Prayer;

public class PrayerTimesResponse {
    public static class Data {
        public static class Timings {
            public String Fajr;
            public String Sunrise;
            public String Dhuhr;
            public String Asr;
            public String Maghrib;
            public String Isha;
        }
        public Timings timings;
        public static class Date {
            public static class Hijri {
                public String date;
                public String day;
                public Month month; // Changed from String to Month class
                public String year;
            }
            public Hijri hijri;
        }
        public Date date;
    }
    public Data data;

    // New Month class to match the API's month object
    public static class Month {
        public int number;
        public String en;
        public String ar;
        public int days;
    }
}
