package models;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

/*
  Here are the methods the client gonna use to send data to server
 */
/**
 *
 * @author Rania
 */
public interface ServerInterface extends java.rmi.Remote {

    // registration or login 
    public void connectClient(ClientInterface inter, Client client) throws java.rmi.RemoteException;

    public void disconnect(ClientInterface inter) throws java.rmi.RemoteException;

    public int CheckClientInfos(Client client, int status) throws RemoteException, IOException;

    public Client getClient(ClientInterface ci) throws java.rmi.RemoteException;

    public void UpdateDisc(Discussion discussion, TextMessage textMessage) throws java.rmi.RemoteException;

    public void AddDisc(Discussion discussion) throws java.rmi.RemoteException;

    public void sendFile(File file, Client client, Discussion discussion) throws java.rmi.RemoteException;

    public void makeCall(VideoCall video) throws java.rmi.RemoteException;

    public void addActiveMember(ArrayList<Client> client, Client activeMember) throws java.rmi.RemoteException;

    public void removeActiveMember(ArrayList<Client> client, Client activeMember) throws java.rmi.RemoteException;

    public boolean isCalling() throws java.rmi.RemoteException;

    public String changeProfilePic(ClientInterface client, File file) throws java.rmi.RemoteException;
    
    public Client LoadAllUserInfosFromDBIntoServer(Client client) throws java.rmi.RemoteException;
}
