package com.example.project;

public class User {
    String name;
    String phone;
    String email;
    String age;
    String group;
    String userId;
    public User(String name, String phone, String email,String group,String age,String userId) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.age=age;
        this.group=group;
        this.userId=userId;
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

    public String getGroup() { return group;}

    public String getUserId() { return userId; }
}
