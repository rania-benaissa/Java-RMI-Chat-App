/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 *
 * @author Rima
 */
public class TextMessage implements Serializable {

    private Client sender;
    private String text;
    private String type;

    public TextMessage(Client sender, String text) throws RemoteException {
        this.sender = sender;
        this.text = text;
        this.type = "text";
    }

    public TextMessage(Client sender, String text, String type) throws RemoteException {

        this.sender = sender;
        this.text = text;
        this.type = type;

    }

    public Client getSender() throws RemoteException {
        return sender;
    }

    public void setSender(Client sender) throws RemoteException {
        this.sender = sender;
    }

    public String getText() throws RemoteException {
        return text;
    }

    public void setText(String text) throws RemoteException {
        this.text = text;
    }

    @Override
    public String toString() {
        return sender + " : " + text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
