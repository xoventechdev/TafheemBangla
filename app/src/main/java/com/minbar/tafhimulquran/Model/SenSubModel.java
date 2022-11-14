package com.minbar.tafhimulquran.Model;

public class SenSubModel {


    String idSurah;
    String idAyat;
    String idTika;

    public SenSubModel(String idSurah, String idAyat, String idTika) {
        this.idSurah = idSurah;
        this.idAyat = idAyat;
        this.idTika = idTika;
    }

    public String getIdSurah() {
        return idSurah;
    }

    public String getIdAyat() {
        return idAyat;
    }

    public String getIdTika() {
        return idTika;
    }
}
