package com.g.apitask.Classes;

public class MyList {

    private String name;
    private String photo;
    private String address;
    private String rating;
    private String cuisines;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }


    public MyList(String name, String photo, String address, String rating, String id){
        this.name = name;
        this.photo = photo;
        this.address = address;
        this.rating = rating;
        this.cuisines = cuisines;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
