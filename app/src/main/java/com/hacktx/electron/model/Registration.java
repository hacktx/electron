package com.hacktx.electron.model;

public class Registration {

    private String name;
    private String email;
    private int age;
    private boolean isCheckedIn;

    public Registration(String name, String email, int age, boolean isCheckedIn) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.isCheckedIn = isCheckedIn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isCheckedIn() {
        return isCheckedIn;
    }

    public void setIsCheckedIn(boolean isCheckedIn) {
        this.isCheckedIn = isCheckedIn;
    }

}
