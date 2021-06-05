package com.company;

import java.io.Serializable;
import java.util.ArrayList;

public class Client implements Serializable {
    private String surname;
    private String name;
    private String lastname;
    private int age;
    private Passport passport;
    private int balance;
    private ArrayList<Integer> bookedRoomsIds = new ArrayList<Integer>();


    public Client(String surname, String name, String lastname, int age, Passport passport, int balance) {
        this.surname = surname;
        this.name = name;
        this.lastname = lastname;
        this.age = age;
        this.passport = passport;
        this.balance = balance;
    }

    public Client(String surname, String name, String lastname, int age, int balance) {
        this.surname = surname;
        this.name = name;
        this.lastname = lastname;
        this.age = age;
        this.passport = passport;
        this.balance = balance;
    }

    public void bookRoom(Room room) {
        this.setBalance(this.getBalance() - room.getPrice());
        this.bookedRoomsIds.add(room.getId());
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Passport getPassport() {
        return passport;
    }

    public void setPassport(Passport passport) {
        this.passport = passport;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
