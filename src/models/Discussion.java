/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author Rima
 */
public class Discussion implements Serializable {

    //Chaque Discu a une liste de clients (les membres) et une liste de messages
    //j'ai préféré créé une classe message pour garder le client qui envoie le message 
    private ArrayList<Client> membres;
    private ArrayList<TextMessage> chat;

    // Constructeur pour soit une discu a 2 ou un groupe doesnt matter
    public Discussion(ArrayList<Client> membres) {

        this.membres = membres;
        chat = new ArrayList<>();
    }

    public ArrayList<Client> getMembres() {
        return membres;
    }

    public void setMembres(ArrayList<Client> membres) {
        this.membres = membres;
    }

    @Override
    public boolean equals(Object other) {

        if (other == null) {
            return false;
        }

        if (this.getClass() != other.getClass()) {
            return false;
        }

        Discussion d = (Discussion) other;
        //membres sont ==  si les clients sont les meme sachant qu'un membre ne peut etre double 
        return this.membres.containsAll(d.membres) && d.membres.containsAll(this.membres);
    }

    @Override
    public String toString() {
        return "DISCU:  " + membres + "\nCHAT:\n" + chat;
    }

    public String print() throws RemoteException {
        String c = "";
        for (TextMessage t : chat) {
            c = c.concat(t.getSender() + " : " + t.getText() + "\n");
        }
        return c;
    }

    public void addText(TextMessage msg) {
        chat.add(msg);
    }

    public ArrayList<TextMessage> getChat() {
        return chat;
    }

    public void setChat(ArrayList<TextMessage> chat) {
        this.chat = chat;
    }

}
