package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 *
 * @author Rima
 */
public class UpdateDB {

    private static final String clients_location = "D:\\M1\\S2\\ProgRéseaux\\Projet_BENAISSA_Rania_MAHMOUDI_Rima\\DataBase\\clients.txt";
    private static final String discussions_location = "D:\\M1\\S2\\ProgRéseaux\\Projet_BENAISSA_Rania_MAHMOUDI_Rima\\DataBase\\discussions.txt";
    private static Gson gson = new Gson();

    // Save to file Utility ----> i only call this method when the client is new so im sure hes not in the DB 
    public static void SaveClient(Client c) throws IOException {
        File ClientsFile = new File(clients_location);
        ArrayList<Client> cc = LoadClients();
        if (cc == null) {
            // if file doesnt exist then i create the list to save in the db 
            cc = new ArrayList<>();
        }
        cc.add(c);

        //Creation du fichier si il n'existe pas 
        if (!ClientsFile.exists()) {
            try {
                File directory = new File(ClientsFile.getParent());
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                ClientsFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Excepton Occured: " + e.toString());
            }
        }

        JsonWriter writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(ClientsFile), "UTF-8"));
        writer.setIndent("    ");

        writer.beginArray();
        for (Client cl : cc) {

            writer.beginObject();
            writer.name("username").value(cl.getUsername());
            writer.name("name").value(cl.getName());
            writer.name("surname").value(cl.getSurname());
            writer.name("password").value(cl.getPassword());
            writer.name("picture").value(cl.getPicture());
            writer.endObject();
        }
        writer.endArray();

        writer.close();
    }

    public static void UpdateClient(Client c, String profilePic) throws IOException {
        File ClientsFile = new File(clients_location);
        if (!ClientsFile.exists()) {
            System.err.println("File Client doesnt exist ---> cannot update client!");
        } else {
            ArrayList<Client> cc = LoadClients();
            if (cc == null) {
                System.err.println("File Client empty ---> cannot update client!");
            } else {
                if (!cc.contains(c)) {
                    System.err.println("Error : Client doesnt exist ---> cannot update client!");
                } else {
                    //UPDATING THE CLIENT
                    cc.get(cc.indexOf(c)).setPicture(profilePic);

                    JsonWriter writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(ClientsFile), "UTF-8"));
                    writer.setIndent("    ");

                    writer.beginArray();
                    for (Client cl : cc) {

                        writer.beginObject();
                        writer.name("username").value(cl.getUsername());
                        writer.name("name").value(cl.getName());
                        writer.name("surname").value(cl.getSurname());
                        writer.name("password").value(cl.getPassword());
                        writer.name("picture").value(cl.getPicture());
                        writer.endObject();
                    }
                    writer.endArray();

                    writer.close();

                }
            }
        }

    }

    //je load tous les clients de la bd ici
    public static ArrayList<Client> LoadClients() throws IOException {
        File clients = new File(clients_location);
        if (!clients.exists()) {
            System.err.println("File doesn't exist");
            return null;
        } else {

            InputStreamReader isReader;
            List<Client> cc = new ArrayList<>();
            try {
                isReader = new InputStreamReader(new FileInputStream(clients), "UTF-8");
                JsonReader myReader = new JsonReader(isReader);

                myReader.beginArray();
                while (myReader.hasNext()) {
                    cc.add(readClient(myReader));
                }
                myReader.endArray();
                isReader.close();
                myReader.close();
                return (ArrayList<Client>) cc;

            } catch (JsonIOException | JsonSyntaxException | FileNotFoundException | UnsupportedEncodingException e) {
                System.err.println("error load cache from file " + e.toString());
            }
        }
        return null;

    }

    //elle me retourne un seul client
    private static Client readClient(JsonReader myReader) throws IOException {

        String username = null;
        String Cname = null;
        String surname = null;
        String psw = null;
        String pic = null;

        myReader.beginObject();

        while (myReader.hasNext()) {

            String name = myReader.nextName();
            if (name.equals("username")) {
                username = myReader.nextString();

            } else if (name.equals("name")) {
                Cname = myReader.nextString();

            } else if (name.equals("surname")) {
                surname = myReader.nextString();

            } else if (name.equals("password")) {
                psw = myReader.nextString();

            } else if (name.equals("picture")) {
                pic = myReader.nextString();

            } else {
                myReader.skipValue();
            }
        }

        myReader.endObject();

        return new Client(username, Cname, surname, psw, pic);

    }

    //client is new so we create new empty Discussions with all possible clients in the BD
    //when client se connecte on load de la bd vers rmi les discussions avec les clients connectés c'est tout
    public static void CreateDiscussionsfor(Client client) throws IOException {

        //on load tous les clients de ma bd
        File ClientsFile = new File(clients_location);
        ArrayList<Client> cc = LoadClients();
        if (cc != null) { //si kayen des clients dans la bd

            ArrayList<Discussion> discussions = new ArrayList<>();
            //pour chaque client de la bd je crée une discu avec mon client
            for (Client cl : cc) {
                ArrayList mems = new ArrayList();
                mems.add(client);
                mems.add(cl);
                Discussion d = new Discussion(mems);
                discussions.add(d);
            }
            UpdateDB.SaveDiscussions(discussions);
        }

    }

    //on load les discussions de la bd de tous les clients 
    //i should have split this to mini-functions but i like to suffer <3 ---------> i had to split, lots of problems
    public static ArrayList<Discussion> LoadDiscus() throws IOException {
        File discus = new File(discussions_location);
        //on verifie si le fichier existe 
        if (!new File(discussions_location).exists()) {
            System.err.println("File doesn't exist");
            return null;
        } else {
            //on va ouvrir un stream et lire su fichier en suivant la structure d'ecriture
            // ----> check discussions.txt
            InputStreamReader isReader;
            isReader = new InputStreamReader(new FileInputStream(discus), "UTF-8");
            JsonReader myReader = new JsonReader(isReader);
            try {
                return readDiscussions(myReader);
            } finally {
                myReader.close();
            }
        }
    }

    private static ArrayList<Discussion> readDiscussions(JsonReader reader) throws IOException {
        ArrayList<Discussion> dis = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            dis.add(readDiscussion(reader));
        }
        reader.endArray();
        return dis;
    }

    private static Discussion readDiscussion(JsonReader myReader) throws IOException {
        Discussion d = null;
        ArrayList<Client> membres = new ArrayList<>();
        ArrayList<TextMessage> texts = new ArrayList<>();

        myReader.beginObject();
        while (myReader.hasNext()) {
            String name = myReader.nextName();
            if (name.equals("clients")) {
                membres = readClientsinDis(myReader);
            } else if (name.equals("messages")) {
                texts = readMsgsinDis(myReader);
            } else {
                myReader.skipValue();
            }
        }
        myReader.endObject();
        d = new Discussion(membres);
        d.setChat(texts);
        return d;

    }

    private static ArrayList<Client> readClientsinDis(JsonReader myReader) throws IOException {
        ArrayList<Client> membres = new ArrayList<>();
        myReader.beginArray();
        while (myReader.hasNext()) {
            //Object = Client
            String username = null;

            myReader.beginObject();
            while (myReader.hasNext()) {
                String name = myReader.nextName();
                if (name.equals("username")) {
                    username = myReader.nextString();
                } else {
                    myReader.skipValue();
                }
            }
            myReader.endObject();
            membres.add(new Client(username, "psw"));
        }
        myReader.endArray();
        return membres;
    }

    private static ArrayList<TextMessage> readMsgsinDis(JsonReader myReader) throws IOException {
        ArrayList<TextMessage> texts = new ArrayList<>();
        myReader.beginArray();
        while (myReader.hasNext()) {
            //Object = TextMessage
            String sender = null;
            String txt = null;
            String type = null;
            myReader.beginObject();
            while (myReader.hasNext()) {
                String name = myReader.nextName();
                if (name.equals("sender_username")) {
                    sender = myReader.nextString();
                } else if (name.equals("msg")) {

                    txt = myReader.nextString();
                } else if (name.equals("type")) {
                    type = myReader.nextString();
                } else {
                    myReader.skipValue();
                }
            }
            myReader.endObject();
            texts.add(new TextMessage(new Client(sender, "psw"), txt, type));
        }
        myReader.endArray();
        return texts;

    }

    //on save les discus de tous les clients du serveur dans la bd 
    //im gonna save just the usernames of the clients cuz its all i need since its unique  ++++++ and the messages ofc
    //je dois verifier si une discu existe dans le serveur et la bd je l'ecrase avec une updated discu
    //si elle existe dans bd et pas dans le serveur je dois la garder
    public static void SaveDiscussions(ArrayList<Discussion> Liste_Discussions) throws FileNotFoundException, IOException {
        File DiscuFile = new File(discussions_location);

        //Creation du fichier si il n'existe pas 
        if (!DiscuFile.exists()) {
            try {
                File directory = new File(DiscuFile.getParent());
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                DiscuFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Exception Occured with the file: " + e.toString());
            }
        } else {
            //si elle existe dans bd et pas dans le serveur je dois la re - save 
            ArrayList<Discussion> existantes = LoadDiscus();
            if (existantes != null) {
                for (Discussion d : existantes) {
                    if (!Liste_Discussions.contains(d)) {
                        Liste_Discussions.add(d);
                    }
                }
            }
        }

        //Notre Stream sur le fichier
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(DiscuFile), "UTF-8"));
        writer.setIndent("    ");

        //Liste des Discussions
        writer.beginArray();
        for (Discussion d : Liste_Discussions) {
            //conditions sur le7biba d avant de save directement 
            //Objet = Discussion
            writer.beginObject();
            //on va ecrire les username de tous nos clients in an array
            writer.name("clients");
            writer.beginArray();
            for (Client c : d.getMembres()) {
                writer.beginObject();
                writer.name("username").value(c.getUsername());
                writer.endObject();
            }
            writer.endArray();
            writer.name("messages");
            writer.beginArray();
            for (TextMessage m : d.getChat()) {
                writer.beginObject();
                writer.name("sender_username").value(m.getSender().getUsername());
                writer.name("msg").value(m.getText());
                writer.name("type").value(m.getType());
                writer.endObject();
            }
            writer.endArray();
            writer.endObject();
        }
        writer.endArray();
        writer.close();
    }

    public static ArrayList<Discussion> LoadDiscussionsFor(Client c) throws IOException {

        //je load les discussions de mon client dans le serveur depuis la bd
        ArrayList<Discussion> All_Dis = UpdateDB.LoadDiscus();

        if (All_Dis != null && !All_Dis.isEmpty()) {
            ArrayList<Discussion> Clients_Dis = new ArrayList<>();
            All_Dis.stream().filter((Discussion d) -> (d.getMembres().contains(c))).forEachOrdered((d) -> {
                Clients_Dis.add(d);
            });

            return Clients_Dis;
        }
        return null;

    }

    //TEST MAIN 
    /*public static void main(String[] args) throws IOException{

            Client c = new Client("sara" , "hello", "dara" , "dara");
            UpdateDB.SaveClient(c);
            UpdateDB.UpdateClient(c, "E:/Miv/S2/Projects/Risox/Version3/src/assets/mami.png");
            
            
        }*/
}
