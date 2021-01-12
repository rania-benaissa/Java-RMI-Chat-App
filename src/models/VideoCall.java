package models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Rania
 */
public class VideoCall implements Serializable {

    //members in the conv
    private ArrayList<Client> members;

    //members who accepted to chat
    private volatile ArrayList<Client> activeMembers;

    private Client sender;

    public VideoCall(ArrayList<Client> members, Client sender) {

        this.activeMembers = new ArrayList<>();

        this.activeMembers.add(sender);

        this.members = new ArrayList<>(members);

        this.sender = sender;
    }

    public ArrayList<Client> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Client> members) {

        this.members = members;

    }

    public Client getSender() {
        return sender;
    }

    public ArrayList<Client> getActiveMembers() {
        return activeMembers;
    }

    public void setActiveMembers(ArrayList<Client> activeMembers) {
        this.activeMembers = activeMembers;
    }

    public void setSender(Client sender) {
        this.sender = sender;
    }

}
