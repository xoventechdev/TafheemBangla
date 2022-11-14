package com.minbar.tafhimulquran.Model;

public class SurahModel {
    String Arabc_Name;
    String Bangla_Name;
    String Sura_Ayat;
    String Sura_BnMean;
    String Sura_Number;
    int Surah_ID;

    public SurahModel(int i, String str, String str2, String str3, String str4, String str5) {
        this.Surah_ID = i;
        this.Sura_Number = str;
        this.Bangla_Name = str2;
        this.Arabc_Name = str3;
        this.Sura_Ayat = str4;
        this.Sura_BnMean = str5;
    }

    public int getSurah_ID() {
        return this.Surah_ID;
    }

    public String getSura_Number() {
        return this.Sura_Number;
    }

    public String getBangla_Name() {
        return this.Bangla_Name;
    }

    public String getArabc_Name() {
        return this.Arabc_Name;
    }

    public String getSura_Ayat() {
        return this.Sura_Ayat;
    }

    public String getSura_BnMean() {
        return this.Sura_BnMean;
    }
}
