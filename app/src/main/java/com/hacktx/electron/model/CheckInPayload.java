package com.hacktx.electron.model;

import com.google.gson.annotations.SerializedName;

public class CheckInPayload {

    @SerializedName("volunteer_email")
    private String volunteerEmail;
    private String email;

    public CheckInPayload(String volunteerEmail, String email) {
        this.volunteerEmail = volunteerEmail;
        this.email = email;
    }

    public String getVolunteerEmail() {
        return volunteerEmail;
    }

    public void setVolunteerEmail(String volunteerEmail) {
        this.volunteerEmail = volunteerEmail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
