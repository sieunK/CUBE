package com.example.cube.Components;

public class PayMenuData {
    private String photo;
    private String name;
    private int price;

    public PayMenuData() {
    }

    public PayMenuData(String photo, String name, int price) {
        this.photo = photo;
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return "PayMenuData{" +
                "photo='" + photo + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                '}';
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}