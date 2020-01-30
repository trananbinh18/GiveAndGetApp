package com.example.giveandgetapp.database;

import androidx.annotation.NonNull;

public class Catalog {
    public int id;
    public String name;

    public Catalog(int id, String name){
        this.id = id;
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
