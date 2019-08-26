package com.example.cube.Components;

public class Menu {
    private int id;
    private String photo;
    private String name;
    private int price;
    private boolean is_soldout;

    public Menu(){
    }

    public Menu(int id, String photo, String name, int price, boolean is_soldout) {
        this.id = id;
        this.photo = photo;
        this.name = name;
        this.price = price;
        this.is_soldout = is_soldout;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id='" + id + '\'' +
                ", photo='" + photo + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", is_soldout=" + is_soldout +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean getIs_soldout() {
        return is_soldout;
    }

    public void setIs_soldout(boolean is_soldout) {
        this.is_soldout = is_soldout;
    }
}
