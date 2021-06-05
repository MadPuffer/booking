package com.company;

import java.io.Serializable;

public class Passport implements Serializable {
    private int number;
    private int series;
    private String surname;
    private String name;
    private String lastname;

    public Passport(int number, int series, String surname, String name, String lastname) {
        this.number = number;
        this.series = series;
        this.surname = surname;
        this.name = name;
        this.lastname = lastname;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
