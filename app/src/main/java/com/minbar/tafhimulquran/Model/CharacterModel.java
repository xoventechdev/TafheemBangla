package com.minbar.tafhimulquran.Model;

public class CharacterModel {

    int id;

    public CharacterModel(int id, String item) {
        this.id = id;
        this.item = item;
    }

    String item;




    public int getId() {
        return id;
    }

    public String getItem() {
        return item;
    }
}
