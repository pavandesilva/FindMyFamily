package com.finalapp.findmyfamily.model;

import android.widget.TextView;

public class UserDetails {
    private String email;
    private String firstname;
    private String lastname;
    private String phone;
    private String address;

    public UserDetails() {
    }

    public UserDetails(String email, String firstname, String lastname, String phone, String address) {
        this.setEmail(email);
        this.setFirstname(firstname);
        this.setLastname(lastname);
        this.setPhone(phone);
        this.setAddress(address);
    }

    @Override
    public String toString() {
        return "user ge object eka";
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }
}
