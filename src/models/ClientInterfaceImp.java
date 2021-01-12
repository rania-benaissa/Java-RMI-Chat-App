/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import animatefx.animation.FadeInRight;
import controllers.WebCamController;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import views.Project;

/**
 *
 * @author Rania
 */
public class ClientInterfaceImp extends UnicastRemoteObject implements ClientInterface {

    //this is my current client
    private Client client;

    //this is to use server's methods -> aka to send to server
    private ServerInterface server;

    //here are all his connected friends
    private static HashMap<ClientInterface, Client> connectedFriends;

    // connected friends also, i just need an observable
    private ObservableList<Client> friends;

    private ObservableList<Discussion> myDiscussions;

    private Pane chatPane;

    private VideoCall video;

    //###################################################################################################################################"
    public ClientInterfaceImp() throws RemoteException, NotBoundException, MalformedURLException {

        this.server = (ServerInterface) Naming.lookup("chat");

        connectedFriends = new HashMap<>();

        friends = FXCollections.observableArrayList();

        myDiscussions = FXCollections.observableArrayList();

        this.video = null;
    }

    @Override
    public void setOnline(ClientInterface inter, Client client) throws RemoteException {

        //LOAD CLIENT DISCUSSIONS FROM DB
        // we add to both of the lists his new connected friend
        connectedFriends.put(inter, client);

        Platform.runLater(
                () -> {
                    friends.add(client);
                });

    }

    @Override
    public Client getClient() throws RemoteException {
        return client;
    }

    public void setClient(Client client) throws RemoteException {
        this.client = client;
    }

    public ServerInterface getServer() throws RemoteException {
        return server;
    }

    public void setServer(ServerInterface server) throws RemoteException {
        this.server = server;
    }

    public ObservableList<Client> getFriends() {
        return friends;
    }

    public void setFriends(ObservableList<Client> friends) throws RemoteException {
        this.friends = friends;
    }

    @Override
    public void setDisconnected(ClientInterface inter) throws RemoteException {

        //kayen une erreur ici quand pour le dernier client quand tous les autres se deconnectent
        Client cl = connectedFriends.remove(inter);

        Platform.runLater(
                () -> {
                    friends.remove(cl);
                });
    }

    @Override
    public void AddDiscussion(Discussion d) throws RemoteException {

        Platform.runLater(
                () -> {
                    myDiscussions.add(d);
                });

    }

    @Override
    public void UpdateDiscussion(Discussion d) throws RemoteException {

        if (myDiscussions.contains(d)) {

            Platform.runLater(
                    () -> {
                        myDiscussions.set(myDiscussions.indexOf(d), d);
                    });

        }

    }

    public ObservableList<Discussion> getMyDiscussions() {
        return myDiscussions;
    }

    @Override
    public void RemoveDiscussion(Discussion d) throws RemoteException {

        if (myDiscussions.contains(d)) {

            Platform.runLater(
                    () -> {
                        myDiscussions.remove(d);
                    });
        }
    }

    @Override
    public void requestCall(VideoCall video) throws RemoteException {

        Platform.runLater(
                () -> {
                    try {

                        //we set the video call
                        WebCamController.setVideoCall(video);

                        this.video = video;

                        Parent webcam = FXMLLoader.load(getClass().getResource("/views/WebCam.fxml"));

                        // add animation
                        new FadeInRight(webcam).play();

                        // set webcam instead of chat
                        chatPane.getChildren().removeAll();
                        chatPane.getChildren().setAll(webcam);

                    } catch (IOException ex) {
                        Logger.getLogger(ClientInterfaceImp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
    }

    public void setChatPane(Pane chatPane) {
        this.chatPane = chatPane;
    }

    @Override
    public void addActiveMember(Client client) throws RemoteException {

        //we add the client to active members
        video.getActiveMembers().add(client);

        WebCamController.setVideoCall(video);

        if (video.getActiveMembers().size() > 1) {

            WebCamController.getMyCam().setReceiverActive(true);
        }
    }

    @Override
    public void removeActiveMember(Client client) throws RemoteException {

        if (video.getActiveMembers().contains(client)) {

            video.getActiveMembers().remove(client);
        }

        WebCamController.setVideoCall(video);

        if (video.getActiveMembers().size() <= 1) {

            if (WebCamController.getMyCam() != null) {

                WebCamController.getMyCam().setReceiverActive(false);
                //this should stop the thread -> and stop the camera
                WebCamController.getMyCam().setActive(false);
            }
            if (WebCamController.getMyCam() != null) {

                WebCamController.getMyCam().closeCam();
            }
            video = null;
            Platform.runLater(
                    () -> {
                        try {
                            //we go back to chat room
                            Parent root = FXMLLoader.load(getClass().getResource("/views/ChatRoom.fxml"));
                            Project.getStage().getScene().setRoot(root);
                        } catch (IOException ex) {
                            Logger.getLogger(ClientInterfaceImp.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
        }
    }

    @Override
    public void updateFriend(ClientInterface inter, Client client) throws RemoteException {

        //we need to update connected friends too
        connectedFriends.put(inter, client);

        //updating friends
        if (friends.contains(client)) {

            Platform.runLater(
                    () -> {
                        friends.set(friends.indexOf(client), client);
                    });

        }
    }

}
