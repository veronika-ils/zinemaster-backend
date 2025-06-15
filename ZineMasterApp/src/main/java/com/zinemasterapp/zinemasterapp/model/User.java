package com.zinemasterapp.zinemasterapp.model;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity//deka se mapira vo tabela vo baza
@Table(name = "users")//tabelata kako se vika vo bazata
public class User {

    @Id//primary key
    private String id;

    private String name;
    private String surname;

    @Column(name = "start_date")
    private LocalDate startDate;


    @Column(name = "user_type")
    private String userType;

    private String address;

    @Column(nullable = false,name = "access")
    private int access;

    @Column(nullable = false,unique = true)
    private String username;

    @Column(name = "password_enc",nullable = false)
    private String password;

    @Column(name = "profile_pic")
    private String profilePic;

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePicture) {
        this.profilePic = profilePicture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }


    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAccess() {//changed the name so its more convenient
        return access;
    }

    public void setAccess(int access) {//no change here
        this.access = access;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
