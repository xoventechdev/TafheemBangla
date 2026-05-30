package com.minbar.tafhimulquran.Prayer;

public class PrayerTimesResponse {
    public static class Data {
        public static class Timings {
            public String Fajr;
            public String Sunrise;
            public String Dhuhr;
            public String Asr;
            public String Sunset;
            public String Maghrib;
            public String Isha;
            public String Imsak;
            public String Midnight;
        }
        public Timings timings;
        
        public static class Date {
            public String readable;
            public String timestamp;
            public static class Hijri {
                public String date;
                public String day;
                public Month month;
                public String year;
            }
            public Hijri hijri;
        }
        public Date date;

        public static class Meta {
            public String timezone;
        }
        public Meta meta;
    }
    public Data data;

    public static class Month {
        public int number;
        public String en;
        public String ar;
    }
}
