package com.rt21.data;

import java.net.UnknownServiceException;
import java.util.UUID;

public class User {
    private String userUUID;
    private String name;
    private String username;
    private String email;
    private String password;

    public User(String name, String username, String email, String password) {
        this.userUUID = UUID.randomUUID().toString().replace("-", "");
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUserUUID() { return userUUID; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    //TODO - setters if needed


    @Override
    public String toString() {
        return "User {\n" +
                "\tuserUUID='" + userUUID + "'\n" +
                "\tname='" + name + "'\n" +
                "\tusername='" + username + "'\n" +
                "\temail='" + email + "'\n" +
                "\tpassword='" + password + "'\n" +
                '}';
    }
}
