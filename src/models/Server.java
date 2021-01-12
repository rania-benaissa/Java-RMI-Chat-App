package models;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *
 * @author Rania
 */
public class Server extends UnicastRemoteObject implements ServerInterface {

    // this contains all the connected clients
    private final HashMap<ClientInterface, Client> clients;

    // i made it an observable cuz when we create a group i wanna be notified 
    private final ObservableList<DiscussionInterfaceImpl> onlinediscussions;

    // check si y a un appel video en cours
    private boolean isCalling;

    private static final String FILES_PATH = "D:\\M1\\S2\\ProgRéseaux\\Projet_BENAISSA_Rania_MAHMOUDI_Rima\\DataBase\\Files\\";

    private static final String PICTURES_PATH = "D:\\M1\\S2\\ProgRéseaux\\Projet_BENAISSA_Rania_MAHMOUDI_Rima\\DataBase\\Pictures\\";

    public Server() throws RemoteException {

        clients = new HashMap<>();

        isCalling = false;

        onlinediscussions = FXCollections.observableArrayList();

        onlinediscussions.addListener((ListChangeListener.Change<? extends DiscussionInterfaceImpl> c) -> {

            while (c.next()) {

                if (c.wasReplaced()) {

                    for (int i = c.getFrom(); i < c.getTo(); ++i) {
                        System.out.println("Repcaled\n" + onlinediscussions.get(i).getDiscu() + "\n");

                        for (ClientInterface ci : clients.keySet()) {

                            if (onlinediscussions.get(i).getDiscu().getMembres().contains(clients.get(ci))) {

                                try {

                                    ci.UpdateDiscussion(onlinediscussions.get(i).getDiscu());

                                    System.err.println(clients.get(ci) + " was updated");

                                } catch (RemoteException ex) {
                                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }
                        }
                    }
                } else if (c.wasAdded()) {
                    for (int i = c.getFrom(); i < c.getTo(); ++i) {
                        System.out.println("Added\n" + onlinediscussions.get(i).getDiscu() + "\n");
                        for (ClientInterface ci : clients.keySet()) {
                            if (onlinediscussions.get(i).getDiscu().getMembres().contains(clients.get(ci))) {
                                try {
                                    ci.AddDiscussion(onlinediscussions.get(i).getDiscu());
                                    System.err.println(clients.get(ci) + " was updated");
                                } catch (RemoteException ex) {
                                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }

                } else if (c.wasRemoved()) {
                    for (int j = 0; j < c.getRemovedSize(); j++) {
                        System.out.println("Removed\n" + c.getRemoved().get(j).getDiscu());

                        for (ClientInterface ci : clients.keySet()) {
                            if (c.getRemoved().get(j).getDiscu().getMembres().contains(clients.get(ci))) {
                                try {
                                    ci.RemoveDiscussion(c.getRemoved().get(j).getDiscu());
                                    System.err.println(clients.get(ci) + " was updated");
                                } catch (RemoteException ex) {
                                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }

                }
            }
        });

    }

    //  message comes from the client part so the server will send this message to be read by all the clients
    @Override
    public synchronized void connectClient(ClientInterface inter, Client client) throws RemoteException {

        //on ramene les infos du clients de la bd
        Client c = LoadAllUserInfosFromDBIntoServer(client);

        if (c != null) {
            // broadcast the client to all the other clients
            for (ClientInterface ci : clients.keySet()) {
                // chaque user connecté on lui rajoute le new client
                ci.setOnline(inter, c);
                // et chacun des users est ajouté a la liste de ce new user
                inter.setOnline(ci, clients.get(ci));
            }

            //on ajoute le new client avec les nouvelles infos dans le serveur
            clients.put(inter, c);
            LoadAllUserDiscussionsFromDBIntoServer(inter, c);

        }

    }

    @Override
    public synchronized void disconnect(ClientInterface inter) throws RemoteException {

        // a chaque deconnexion je dois save les discus du client dans la bd 
        //i cant iterate and remove so im doing this
        ArrayList<Discussion> discusClient = new ArrayList<>(); //a save dans la bd
        ArrayList<DiscussionInterfaceImpl> discus = new ArrayList<>(); // a supp du serveur

        if (!onlinediscussions.isEmpty()) {
            for (DiscussionInterfaceImpl d : onlinediscussions) {
                //si c'est un discu du client je la save dans la bd et je la supp du serveur
                if (d.getDiscu().getMembres().contains(clients.get(inter))) {
                    discusClient.add(d.getDiscu());
                    discus.add(d);
                }
            }
            try {
                // i save the discussions of this client in the db
                UpdateDB.SaveDiscussions(discusClient);
                // i remove them from the serveur
                onlinediscussions.removeAll(discus);

            } catch (IOException ex) {
                System.err.println("Cannot save discus after deconnexion!" + ex);
            }
        } else {
            System.err.println("No discussions to save in the bd");
        }

        // gonna loop through the list of connected user and tell them that this client is disconnected
        for (ClientInterface ci : clients.keySet()) {
            // on notifie tous les clients presents que inter n'est plus
            ci.setDisconnected(inter);
        }
        //on l'enleve de la liste
        clients.remove(inter);

    }

    @Override
    public synchronized int CheckClientInfos(Client client, int status) throws RemoteException, IOException {
        // before that he s supposed to check in the bdd if the client exists or not and if he exists, checks if his password is correct 
        //STATUS == 1 LOGIN
        //doesnt exist ---> 1
        // wrong psw -----> 2
        //everything ok --> 0

        //STATUS == 2 REGISTRATION
        //already exists ----> 3
        //ok ---------------> 4
        //Client doesnt exist 
        if (UpdateDB.LoadClients() == null || !UpdateDB.LoadClients().contains(client)) {
            if (status == 1) {
                return 1; //u dont exist
            }
            if (status == 2) {
                //Creation in DataBase For Registration, puisque il exite pas donc je crée ses discussions avec les autres clients
                UpdateDB.CreateDiscussionsfor(client);
                // je lenregistre dans la bd 
                UpdateDB.SaveClient(client);
                return 4;
            }
        } else // client exists 
        {
            if (status == 1) {

                //CHECK PASSWORD IS WRONG
                Client existant = UpdateDB.LoadClients().get(UpdateDB.LoadClients().indexOf(client));

                if (!existant.getPassword().equals(client.getPassword())) {
                    return 2;
                } else {
                    return 0;
                }
            }
            if (status == 2) {
                return 3;
            }

        }

        return -1; //ERROR
    }

    //i get all the informations about the user in the db
    @Override
    public synchronized Client LoadAllUserInfosFromDBIntoServer(Client client) {

        //loading the client
        Client existant = null;
        try {

            if (UpdateDB.LoadClients().contains(client)) {

                existant = UpdateDB.LoadClients().get(UpdateDB.LoadClients().indexOf(client));
            }
        } catch (IOException ex) {
            System.err.println("Error Loading Client " + ex);
        }

        return existant;

    }

    //im loading from the database directly to server and client (:
    private synchronized void LoadAllUserDiscussionsFromDBIntoServer(ClientInterface inter, Client client) throws RemoteException {

        try {
            //can be if ure the first one to register
            if (UpdateDB.LoadDiscussionsFor(client) != null) {
                for (int i = 0; i < UpdateDB.LoadDiscussionsFor(client).size(); i++) {
                    //updating discussion infos like name... from the server since i already loaded them from clients.txt 
                    //cuz discussions.txt just contains the username
                    Discussion d = UpdateDB.LoadDiscussionsFor(client).get(i);

                    //i load the discussion if all the members are online sinon i dont load it -----> optimisation
                    boolean online = true;
                    int k = 0;
                    //ALL MEMBERS MUST BE ONLINE
                    while (online && k < d.getMembres().size()) {
                        if (!clients.containsValue(d.getMembres().get(k))) {
                            online = false;
                        }
                        k++;
                    }
                    if (online) {
                        onlinediscussions.add(new DiscussionInterfaceImpl(d));
                    } else {
                        System.err.println("Not All members are online");
                    }

                }
            }

        } catch (IOException ex) {
            System.err.println("Error in Add Discussion :3" + ex.toString());
        }

    }

    //i get the interface of client from the ui and return the client's information from the server
    @Override
    public synchronized Client getClient(ClientInterface ci) throws RemoteException {

        return clients.get(ci);
    }

    @Override
    public synchronized void UpdateDisc(Discussion discussion, TextMessage textMessage) throws RemoteException {

        //im gonna get the discussion from the server and add the new text to it 
        //just to be sure
        if (onlinediscussions.contains(new DiscussionInterfaceImpl(discussion))) {

            //i get the index
            int i = onlinediscussions.indexOf(new DiscussionInterfaceImpl(discussion));
            //---->Fired Events Replaced / ADD / REMOVED
            DiscussionInterfaceImpl d = onlinediscussions.get(i);
            if (textMessage != null) {
                d.AddMsg(textMessage);
            }
            onlinediscussions.set(i, d);
        } else {
            System.err.println("Discussion doesnt exit so cannot be updated");
        }

    }

    @Override
    public synchronized void AddDisc(Discussion discussion) throws RemoteException {

        if (!onlinediscussions.contains(new DiscussionInterfaceImpl(discussion))) {
            onlinediscussions.add(new DiscussionInterfaceImpl(discussion));
        }
    }

    @Override
    public void sendFile(File file, Client client, Discussion discussion) throws RemoteException {

        try {

            //get number of files in "Files" to use it as an id
            int number = new File(FILES_PATH).list().length + 1;

            //get the file name without extension
            String name = file.getName().substring(0, file.getName().lastIndexOf("."));
            // get the extension
            String extension = file.getName().substring(file.getName().lastIndexOf("."), file.getName().length());

            //path of the destination file
            String path = FILES_PATH + name + "_" + number + extension;

            Path source = Paths.get(file.getAbsolutePath());
            Path dest = Paths.get(path);

            //we copy the file in the server 
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);

            //and we get the file back to all the discussion's members
            UpdateDisc(discussion, new TextMessage(client, path, "file"));

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void makeCall(VideoCall video) throws RemoteException {

        for (Client member : video.getMembers()) {

            for (ClientInterface ci : clients.keySet()) {

                if (clients.get(ci).equals(member)) {

                    ci.requestCall(video);

                    break;
                }

            }
        }

    }

    @Override
    public void addActiveMember(ArrayList<Client> cl, Client activeMember) throws RemoteException {

        for (Client member : cl) {

            for (ClientInterface ci : clients.keySet()) {

                if (clients.get(ci).equals(member)) {

                    ci.addActiveMember(activeMember);

                    break;
                }

            }
        }

        isCalling = true;
    }

    @Override
    public void removeActiveMember(ArrayList<Client> cl, Client activeMember) throws RemoteException {

        for (Client member : cl) {

            for (ClientInterface ci : clients.keySet()) {

                if (clients.get(ci).equals(member)) {

                    ci.removeActiveMember(activeMember);

                    break;
                }

            }
        }

        isCalling = false;
    }

    @Override
    public boolean isCalling() throws RemoteException {

        return isCalling;
    }

    @Override
    public String changeProfilePic(ClientInterface client, File file) throws RemoteException {
        try {
            //get number of files in "Files" to use it as an id
            int number = new File(PICTURES_PATH).list().length + 1;

            //get the file name without extension
            String name = file.getName().substring(0, file.getName().lastIndexOf("."));
            // get the extension
            String extension = file.getName().substring(file.getName().lastIndexOf("."), file.getName().length());

            //path of the destination file
            String path = PICTURES_PATH + name + "_" + number + extension;

            Path source = Paths.get(file.getAbsolutePath());
            Path dest = Paths.get(path);

            //we copy the file in the server
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);

            //updating the server ----> working
            if (clients.containsKey(client)) {

                Client u = clients.get(client);

                u.setPicture(path);

                for (ClientInterface ci : clients.keySet()) {

                    ci.updateFriend(ci, u);
                }

            } else {
                System.err.println("Discussion doesnt exit so cannot be updated");
            }

            /*and here we should change the path in the bd -> it should set to "path" variable*/
            UpdateDB.UpdateClient(getClient(client), path);

            return path;

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "";
    }

    public static void main(String[] args) throws RemoteException, MalformedURLException {

        Server server = new Server();

        System.out.println("Server is successfully registred !");

        Naming.rebind("chat", server);

        System.out.println("Server is ready.. waiting for clients!");
    }

}
