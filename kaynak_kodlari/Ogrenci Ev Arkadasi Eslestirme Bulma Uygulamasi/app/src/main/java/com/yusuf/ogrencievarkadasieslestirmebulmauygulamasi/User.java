package com.yusuf.ogrencievarkadasieslestirmebulmauygulamasi;

public class User {
    private String userId;
    private String name;
    private String surname;
    private String education;
    private String distanceToCampus;
    private String stayDuration;
    private String status;

    private String locationX;
    private String locationY;

    public User() {
        // Boş parametresiz yapıcı metod (gereklidir)
    }

    public User(String userId, String name, String surname, String education, String distanceToCampus, String stayDuration, String status) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.education = education;
        this.distanceToCampus = distanceToCampus;
        this.stayDuration = stayDuration;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getDistanceToCampus() {
        return distanceToCampus;
    }

    public void setDistanceToCampus(String distanceToCampus) {
        this.distanceToCampus = distanceToCampus;
    }

    public String getStayDuration() {
        return stayDuration;
    }

    public void setStayDuration(String stayDuration) {
        this.stayDuration = stayDuration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
