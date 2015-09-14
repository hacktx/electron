package com.hacktx.electron.model;

public class Attendee {

    private String name;
    private String email;
    private int age;
    private boolean waiverSigned;
    private boolean isCheckedIn;

    public Attendee(String name, String email, int age, boolean waiverSigned, boolean isCheckedIn) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.waiverSigned = waiverSigned;
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

}