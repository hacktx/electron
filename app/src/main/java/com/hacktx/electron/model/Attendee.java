package com.hacktx.electron.model;

import com.google.gson.annotations.SerializedName;

public class Attendee {

    private String name;
    private String school;
    private String email;
    private int age;
    @SerializedName("waiver_signed")
    private boolean waiverSigned;
    @SerializedName("checked_in")
    private boolean isCheckedIn;
    private boolean confirmed;

    public Attendee(String name, String school, String email, int age, boolean waiverSigned, boolean isCheckedIn, boolean confirmed) {
        this.name = name;
        this.school = school;
        this.email = email;
        this.age = age;
        this.waiverSigned = waiverSigned;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isWaiverSigned() {
        return waiverSigned;
    }

    public void setWaiverSigned(boolean waiverSigned) {
        this.waiverSigned = waiverSigned;
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
