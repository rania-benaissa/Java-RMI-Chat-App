package models;

import java.rmi.RemoteException;


/* Here are the methods that the server gonna use to send data to client



/**
 *
 * @author Rania
 */
public interface ClientInterface extends java.rmi.Remote {

    public void setOnline(ClientInterface inter, Client client) throws java.rmi.RemoteException;

    public void setDisconnected(ClientInterface inter) throws java.rmi.RemoteException;

    public void AddDiscussion(Discussion d) throws java.rmi.RemoteException;

    public void UpdateDiscussion(Discussion d) throws RemoteException;

    public void RemoveDiscussion(Discussion d) throws java.rmi.RemoteException;

    public Client getClient() throws RemoteException;

    public void requestCall(VideoCall video) throws RemoteException;

    public void addActiveMember(Client client) throws java.rmi.RemoteException;

    public void removeActiveMember(Client client) throws java.rmi.RemoteException;

    public void updateFriend(ClientInterface inter ,Client client)throws java.rmi.RemoteException;
}
