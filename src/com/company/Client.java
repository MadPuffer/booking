package com.company;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class Client implements Serializable {
    private String surname;
    private String name;
    private String lastname;
    private int age;
    private Passport passport;
    private int balance;
    private ArrayList<Room> bookedRooms = new ArrayList<Room>();


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
        this.balance = balance;
    }

    public void bookRoom(Room room, int koef) throws IOException {
        Main.writeLog(String.format("%s оплатил комнату на счет %d руб.", this.name, room.getPrice() * koef));
        this.setBalance(this.getBalance() - (room.getPrice() * koef));
        this.bookedRooms.add(room);
    }

    public ArrayList<Room> getBookedRooms() {
        return bookedRooms;
    }

    public void setBookedRooms(ArrayList<Room> bookedRooms) {
        this.bookedRooms = bookedRooms;
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
