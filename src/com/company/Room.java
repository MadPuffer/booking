package com.company;

import java.io.Serializable;
import java.time.LocalDate;

public class Room implements Serializable {
    private int id;
    private int price;
    private int capacity;
    private RoomType roomType;
    private boolean isBooked = false;
    private String name;
    private int livingClientsCount;
    private LocalDate exitDate;

    public Room(int id, RoomType roomType) {
        this.id = id;
        this.roomType = roomType;

        switch (roomType) {
            case SINGLE:
                this.capacity = 1;
                this.price = 1500;
                this.name = "Одноместный номер";
                break;
            case DOUBLE:
                this.capacity = 2;
                this.price = 2500;
                this.name = "Двухместный номер";
                break;
            case WITHBABY:
                this.capacity = 3;
                this.price = 3300;
                this.name = "Двухместный номер + ребенок";
                break;
            case GENERAL:
                this.capacity = 6;
                this.price = 500;
                this.name = "Общий номер";
                break;
        }
    }

    @Override
    public String toString() {
        return String.format("%s:\n -Номер комнаты: %d\n -Цена: %dр.\n -Вместимость: %d человек(а)", this.name,
                this.id + 1, this.price, this.capacity);
    }

    public LocalDate getExitDate() {
        return exitDate;
    }

    public void setExitDate(LocalDate exitDate) {
        this.exitDate = exitDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLivingClientsCount() {
        return livingClientsCount;
    }

    public void setLivingClientsCount(int livingClientsCount) {
        this.livingClientsCount = livingClientsCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
}