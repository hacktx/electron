package com.hacktx.electron.model;

import com.google.gson.annotations.SerializedName;

public class Attendee {

    private String name;
    private String school;
    private String email;
    private String birthday;
    private int age;
    @SerializedName("checked_in")
    private boolean isCheckedIn;
    private boolean confirmed;

    public Attendee(String name, String school, String email, String birthday, int age, boolean waiverSigned, boolean isCheckedIn, boolean confirmed) {
        this.name = name;
        this.school = school;
        this.email = email;
        this.birthday = birthday;
        this.age = age;
        this.isCheckedIn = isCheckedIn;
        this.confirmed = confirmed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
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

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

}
