package com.example.project;

public class User {
    String name;
    String phone;
    String email;
    String age;
    String group1;
    String userId;
    String location;
    String date;
    String availability;
    public User(String name, String phone, String email,String group,String age,String userId,String location,String date,String availability) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.age=age;
        this.group1=group;
        this.userId=userId;
        this.location=location;
        this.date=date;
        this.availability=availability;
    }

    public String getName() {
        return name;
    }
    public String getPhone() {
        return phone;
    }
    public String getEmail() {
        return email;
    }
    public String getAge() { return age; }
    public String getGroup() { return group1;}
    public String getUserId() { return userId; }
    public String getLocation() { return location; }
    public String getDate(){return date;}
    public String getAvailability(){return availability;}
}
