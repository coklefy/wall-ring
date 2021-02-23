package com.wallring.Model;


public class User {
    private String Name;
    private String Password;
    private String SecureCode;


    public User(){

    }

    public User(String name, String password, String secureCode) {
        Name = name;
        Password = password;
        SecureCode = secureCode;
    }

    public String getSecureCode() {
        return SecureCode;
    }

    public void setSecureCode(String secureCode) {
        SecureCode = secureCode;
    }

    public String getName() {
        return Name;
    }

    public String getPassword() {
        return Password;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
