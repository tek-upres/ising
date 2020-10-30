package com.pi.ising.model;

public class Utilisateur {
    //private String id;
    private String fullName;
    private String username;
    private String email;
    private String password;
    private String city;
    private String userRole;
    private String birthday;
    private String numberPhone;

    public Utilisateur(/*String id,*/ String fullName, String username, String email, String password, String city, String userRole, String birthday,String numberPhone) {
        //this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.city = city;
        this.userRole = userRole;
        this.birthday = birthday;
        this.numberPhone=numberPhone;
    }


///////////////Getters/////////

    public String getNumberPhone() {
        return numberPhone;
    }

    /*public String getId() {
        return id;
    }*/

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getCity() {
        return city;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getBirthday() {
        return birthday;
    }
///////////////////////Setters///////////


    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    /*public void setId(String id) {
        this.id = id;
    }*/

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
