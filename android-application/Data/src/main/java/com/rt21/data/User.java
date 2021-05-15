package com.rt21.data;

import java.net.UnknownServiceException;
import java.util.UUID;

public class User {
    private String _id;
    private String name;
    private String username;
    private String email;
    private int age;

    public User(String id, String name, String username, String email, int age) {
        this._id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.age = age;
    }

    public String getId() { return _id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public int getAge() { return age; }

    @Override
    public String toString() {
        return "User {\n" +
                "\t_id='" + _id + "'\n" +
                "\tname='" + name + "'\n" +
                "\tusername='" + username + "'\n" +
                "\temail='" + email + "'\n" +
                "\tage='" + age + "'\n" +
                '}';
    }
}
