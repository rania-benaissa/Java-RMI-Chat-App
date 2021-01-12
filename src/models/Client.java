package models;

import java.io.Serializable;

/**
 *
 * @author Rania
 */
public class Client implements Serializable {

    private String username;
    private String password;
    private String picture;
    private String name, surname;

    public Client(String username, String pwd) {

        this.username = username;
        this.password = pwd;
        this.picture = "/assets/user_picture.png";
    }

    public Client(String username, String pwd, String picPath) {

        this.username = username;
        this.password = pwd;
        this.picture = picPath;
    }

    public Client(String username, String pwd, String name, String surname) {

        this.username = username;
        this.password = pwd;
        this.name = name;
        this.surname = surname;
        this.picture = "/assets/user_picture.png";
    }

    public Client(String username) {
        this.username = username;
    }

    Client(String username, String name, String surname, String psw, String pic) {
        this.username = username;
        this.password = psw;
        this.name = name;
        this.surname = surname;
        this.picture = pic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "@" + username;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (this.getClass() != other.getClass()) {
            return false;
        }

        Client c = (Client) other;
        return this.username.equalsIgnoreCase(c.username);

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

}
