package com.pfc.pojos;

import androidx.annotation.NonNull;

public class User {

    private String email;
    private String city;
    private String phone;
    private String avatar;

    public User() {
    }

    public User(String email, String city, String phone, String avatar) {
        this.email = email;
        this.city = city;
        this.phone = phone;
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}//End