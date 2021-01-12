package controllers;

import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import models.Client;
import models.ClientInterfaceImp;
import models.CustomFile;
import models.Discussion;
import models.TextMessage;
import models.VideoCall;
import views.Project;

/**
 * FXML Controller class
 *
 * @author Rania
 */
public class ChatRoomController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private Circle profilePic;
    @FXML
    private Label UserInfos;
    @FXML
    private Label username;
    @FXML
    private JFXTextField GroupsSearchbar;
    @FXML
    private ListView<Discussion> groupsList;
    @FXML
    private Label DestinataireName;
    @FXML
    private JFXTextField textMessage;
    @FXML
    private ListView<Client> onlineUsers;
    @FXML
    private ImageView go;
    @FXML
    private VBox ChatContainer;
    @FXML
    private JFXTextField UsersSearchBar;

    @FXML
    private ListView<File> filesList;
    @FXML
    private Pane chatPane;

    /* those three are used to drag the window*/
    private double xOffset = 0;
    private double yOffset = 0;

    private ClientInterfaceImp currentClient;

    private ArrayList<Client> CurrentMembers = new ArrayList<>();

    private Discussion CurrentDiscussion;

    private ObservableList<Discussion> groups;

    private VideoCall videoCall;
    @FXML
    private ImageView videoCallButton;
    @FXML
    private VBox VboxContainer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // make the screen draggable
        dragScreen();

        try {
            Init();
        } catch (FileNotFoundException ex) {
            System.err.println("Picture not found" + ex);
        }

        //add connected users to listView
        populateConnectedUsers();

        populategroups();

        UpdateDiscussions();

        LaunchListners();

        //ILL DO THE GROUPS SEARCHBAR LATER
    }

    private void Init() throws FileNotFoundException {
        try {

            VboxContainer.getChildren().remove(1);

            //get the current client
            currentClient = Project.getClient();

            currentClient.setChatPane(chatPane);

            // set the client's username
            username.setText("@" + currentClient.getClient().getUsername());

            UserInfos.setText(currentClient.getClient().getSurname() + " " + currentClient.getClient().getName());

            Image im;
            if (currentClient.getClient().getPicture().equals("/assets/user_picture.png")) {
                im = new Image(currentClient.getClient().getPicture(), false);
            } else {
                im = new Image(new FileInputStream(currentClient.getClient().getPicture()));
            }

            profilePic.setFill(new ImagePattern(im));

            ChatContainer.setStyle("-fx-background-color: transparent");

            //set the current discussion
            CurrentMembers.add(new Client("No Data"));

            CurrentDiscussion = new Discussion(CurrentMembers);

            printDiscussion(CurrentDiscussion);

            groupsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        } catch (RemoteException ex) {
            Logger.getLogger(ChatRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void populateConnectedUsers() {
        // Create a list of clients
        ObservableList<Client> onlineClients = Project.getClient().getFriends();

        // Setup the CellFactory
        onlineUsers.setCellFactory(listView -> new ListCell<Client>() {

            @Override
            protected void updateItem(Client client, boolean empty) {

                super.updateItem(client, empty);

                if (empty || client == null) {

                    setGraphic(null);
                } else {

                    try {
                        // Create a HBox to hold our displayed values
                        HBox hBox = new HBox();

                        hBox.setAlignment(Pos.CENTER_LEFT);

                        hBox.setSpacing(20);

                        Circle circle = new Circle(20);

                        Image im;
                        if (client.getPicture().equals("/assets/user_picture.png")) {
                            im = new Image(client.getPicture(), false);
                        } else {
                            im = new Image(new FileInputStream(client.getPicture()));
                        }

                        circle.setFill(new ImagePattern(im));

                        // Add the values  to the HBox
                        hBox.getChildren().addAll(
                                circle,
                                new Label(client.getUsername())
                        );
                        // Set the HBox as the display
                        setGraphic(hBox);
                    } catch (FileNotFoundException ex) {
                        System.err.println("CANNOT UPDATE PICTUURE IN ONLINE USERS" + ex);
                    }

                }
            }
        });

        onlineUsers.setItems(onlineClients);

    }

    public void populateFilesList(List<File> files) {

        VboxContainer.getChildren().add(filesList);

        // Create a list of files
        ObservableList<File> selectedFiles = FXCollections.observableArrayList(files);
        // Setup the CellFactory
        filesList.setCellFactory(listView -> new ListCell<File>() {

            @Override
            protected void updateItem(File file, boolean empty) {

                super.updateItem(file, empty);

                if (empty || file == null) {

                    setGraphic(null);

                } else {

                    String name = file.getName().substring(0, file.getName().lastIndexOf("."));

                    CustomFile custom = new CustomFile(file, name);

                    Label label = new Label("X");

                    label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

                    label.setAlignment(Pos.TOP_RIGHT);

                    label.setCursor(Cursor.HAND);

                    label.setOnMouseClicked(event -> {

                        getListView().getItems().remove(getItem());

                        if (getListView().getItems().isEmpty()) {

                            VboxContainer.getChildren().remove(1);

                        }

                    }
                    );

                    // Add the label to the HBox
                    custom.getHbox().getChildren().addAll(
                            label
                    );
                    // Set the HBox as the display
                    setGraphic(custom.getHbox());

                }
            }
        });

        filesList.setItems(selectedFiles);
    }

    private void populategroups() {

        ObservableList<Discussion> dd = currentClient.getMyDiscussions();

        groups = FXCollections.observableArrayList(dd);

        groups.removeIf(Discussion -> Discussion.getMembres().size() < 3);

        groupsList.setCellFactory(listView -> new ListCell<Discussion>() {

            @Override
            protected void updateItem(Discussion d, boolean empty) {

                super.updateItem(d, empty);

                if (empty || d == null) {

                    setGraphic(null);

                } else if (d.getMembres().size() > 2) {

                    // Create a HBox to hold our displayed values
                    VBox hBox = new VBox();
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.setSpacing(10);

                    Label label = new Label("");
                    label.setWrapText(true);
                    label.setAlignment(Pos.CENTER);

                    // Add the values  to the HBox
                    for (Client f : d.getMembres()) {

                        label.setText(label.getText() + f.getUsername() + ",");
                    }

                    label.setText(label.getText().substring(0, label.getText().length() - 1));
                    label.setStyle("-fx-font-size: 14px;" + "-fx-font-weight: bold;");
                    label.setAlignment(Pos.CENTER);

                    hBox.getChildren().add(label);
                    hBox.setAlignment(Pos.CENTER);
                    // Set the HBox as the display
                    setGraphic(hBox);

                } else {
                    return;
                }
            }
        });
        groupsList.setItems(groups);
    }

    private void UpdateDiscussions() {

        ObservableList<Discussion> dd = currentClient.getMyDiscussions();

        dd.addListener((ListChangeListener.Change<? extends Discussion> c) -> {

            while (c.next()) {

                if (c.wasReplaced()) {

                    for (int i = c.getFrom(); i < c.getTo(); ++i) {

                        System.out.println("Repcaled\n" + dd.get(i) + "\n");

                        // if im updating a group
                        if (dd.get(i).getMembres().size() > 2) {

                            groups.set(groups.indexOf(dd.get(i)), dd.get(i));
                        }

                        //if im in this discussion and its updated display the new one
                        if (CurrentDiscussion.equals(dd.get(i))) {

                            CurrentDiscussion = dd.get(i);

                            printDiscussion(CurrentDiscussion);

                        } else {

                            System.err.println("Not concerned!");
                        }
                    }
                } else if (c.wasAdded()) {

                    for (int i = c.getFrom(); i < c.getTo(); ++i) {

                        System.out.println("Added\n" + dd.get(i) + "\n");

                        if (dd.get(i).getMembres().size() > 2) {

                            groups.add(dd.get(i));
                        }
                    }
                } else if (c.wasRemoved()) {

                    for (int j = 0; j < c.getRemovedSize(); j++) {

                        System.out.println("Removed\n" + (Discussion) c.getRemoved().get(j));

                        //if im removing a group
                        if (c.getRemoved().get(j).getMembres().size() > 2) {

                            groups.remove(c.getRemoved().get(j));

                        }
                        //if i was displaying it and it got removed then i display no data
                        if (CurrentDiscussion.equals((Discussion) c.getRemoved().get(j))) {

                            //there is an exception here ----> Fixed
                            Platform.runLater(
                                    () -> {
                                        DestinataireName.setText("No Data");
                                        ChatContainer.getChildren().removeAll(ChatContainer.getChildren());
                                    });
                        } else {
                            System.err.println("Not concerned!");
                        }
                    }
                }
            }

        });

    }

    private void LaunchListners() {

        //THE SEARCH BAR FOR USERS
        UsersSearchBar.textProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                search((String) oldValue, (String) newValue);
            }
        });

        //THE SEARCH BAR FOR USERS
        GroupsSearchbar.textProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                searchGroup((String) oldValue, (String) newValue);
            }
        });

        onlineUsers.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {

                    //when i click on listview i search for the discussion and i print it 
                    CurrentMembers = new ArrayList<>();
                    CurrentMembers.add(currentClient.getClient());
                    CurrentMembers.add(onlineUsers.getSelectionModel().getSelectedItem());
                    CurrentDiscussion = new Discussion(CurrentMembers);

                    //if it exists 
                    if (currentClient.getMyDiscussions().contains(CurrentDiscussion)) {

                        //enable video call discussion
                        videoCallButton.setDisable(false);

                        CurrentDiscussion = currentClient.getMyDiscussions().get(currentClient.getMyDiscussions().indexOf(CurrentDiscussion));
                        printDiscussion(CurrentDiscussion);

                    } else {
                        DestinataireName.setText("No Data");
                        ChatContainer.getChildren().removeAll(ChatContainer.getChildren());
                    }
                } catch (RemoteException ex) {
                    System.err.println("IDK " + ex);
                }

            }
        });

        groupsList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (groupsList.getSelectionModel().getSelectedItem() != null) {

                    CurrentDiscussion = new Discussion(groupsList.getSelectionModel().getSelectedItem().getMembres());
                    //if it exists //JUST CHECKING

                    if (currentClient.getMyDiscussions().contains(CurrentDiscussion)) {

                        //i disable the video call
                        videoCallButton.setDisable(true);

                        CurrentDiscussion = currentClient.getMyDiscussions().get(currentClient.getMyDiscussions().indexOf(CurrentDiscussion));
                        printDiscussion(CurrentDiscussion);
                    } else {
                        DestinataireName.setText("No Data");
                        ChatContainer.getChildren().removeAll(ChatContainer.getChildren());
                    }
                }

            }
        }
        );

        currentClient.getFriends().addListener((ListChangeListener.Change<? extends Client> c) -> {

            populateConnectedUsers();

        });
    }

    //THIS IS THE FUNCTION THAT IS DISPLAYING THE CHAT 
    private void printDiscussion(Discussion d) {

        //THIS THING IS IMPORTANT CUZ IT CAUSES AN EXCEPTION CUZ WE UPDATE THE UI ILLEGALLY 
        Platform.runLater(
                () -> {
                    try {
                        String dt = "";

                        ChatContainer.getChildren().removeAll(ChatContainer.getChildren());

                        for (Client c : d.getMembres()) {
                            // im updating from the db directement 
                            c = currentClient.getServer().LoadAllUserInfosFromDBIntoServer(c);
                            if (c != null && !c.getUsername().equals(currentClient.getClient().getUsername())) {

                                dt = dt.concat(c.getName() + " " + c.getSurname() + "    ");
                            }

                        }

                        DestinataireName.setText(dt);

                        for (TextMessage txt : d.getChat()) {

                            Label username = new Label(txt.getSender().getUsername());
                            username.setTextAlignment(TextAlignment.RIGHT);
                            username.setTextFill(Paint.valueOf("gray"));
                            Client sender = currentClient.getServer().LoadAllUserInfosFromDBIntoServer(txt.getSender());

                            Circle circle = new Circle(20);

                            Image im;
                            if (sender.getPicture().equals("/assets/user_picture.png")) {
                                im = new Image(sender.getPicture(), false);
                            } else {
                                im = new Image(new FileInputStream(sender.getPicture()));
                            }
                            circle.setFill(new ImagePattern(im));

                            if (txt.getType().equals("text")) {

                                printTexts(txt, circle, username);

                            } else {
                                if (txt.getType().equals("file")) {

                                    printFiles(txt, circle, username);
                                }
                            }
                        }
                    } catch (RemoteException ex) {
                        System.err.println("PROB GETTING THE CHAT" + ex);
                    } catch (FileNotFoundException ex) {
                        System.err.println("Picture not found line 531" + ex);
                    }
                }
        );

    }

    public void printTexts(TextMessage txt, Circle circle, Label username) throws RemoteException {

        HBox MC = new HBox();

        VBox TC = new VBox();

        Label msg = new Label(txt.getText());

        msg.setPadding(new Insets(5, 10, 5, 10));
        msg.setTextAlignment(TextAlignment.LEFT);

        TC.getChildren().addAll(username, msg);

        msg.setWrapText(true);
        msg.setMinHeight(Region.USE_PREF_SIZE);
        msg.setMinWidth(0);
        msg.setMaxWidth(200);

        if (txt.getSender().getUsername().equals(currentClient.getClient().getUsername())) {

            MC.setAlignment(Pos.BOTTOM_RIGHT);
            msg.setStyle("-fx-background-radius :20;"
                    + "-fx-background-color : #00838e;"
                    + "-fx-text-fill:white");

            MC.getChildren().addAll(
                    TC,
                    circle
            );
        } else {

            MC.setAlignment(Pos.BOTTOM_LEFT);

            msg.setStyle("-fx-background-radius :20;"
                    + "-fx-background-color : #9e9e9e;"
                    + "-fx-text-fill:white");

            MC.getChildren().addAll(
                    circle,
                    TC
            );
        }

        //ESPACE DU vbox
        MC.setPadding(new Insets(10, 20, 10, 10));

        // ESPACE DE SES ELEMENTS
        MC.setSpacing(10);

        ChatContainer.getChildren().add(MC);

    }

    public void printFiles(TextMessage txt, Circle circle, Label username) throws RemoteException {

        File file = new File(txt.getText());

        //GET THE FILE NAME
        String name = file.getName().substring(0, file.getName().lastIndexOf("_"));

        CustomFile custom = new CustomFile(file, name);

        //VBOX CONTAINING THE FILE + SENDER S NAME
        VBox vbox = new VBox();
        vbox.getChildren().addAll(username, custom.getVbox());
        custom.getHbox().getChildren().removeAll();

        //get the file's extension
        String extension = file.getName().substring(file.getName().lastIndexOf("."), file.getName().length());

        //on click i have to save the file in my own computer
        custom.getVbox().setOnMouseClicked((e) -> {

            // we let the user select where to save the file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save file");
            fileChooser.setInitialFileName(name);

            // Set extension filter
            FileChooser.ExtensionFilter extFilter
                    = new FileChooser.ExtensionFilter("(*" + extension + ")", "*" + extension);
            fileChooser.getExtensionFilters().add(extFilter);

            //Show save file dialog
            File f = fileChooser.showSaveDialog(Project.getStage());

            if (f != null) {

                try {
                    Path source = Paths.get(file.getAbsolutePath());
                    Path dest = Paths.get(f.getAbsolutePath());

                    //we copy the file from the server
                    Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);

                } catch (IOException ex) {
                    Logger.getLogger(ClientInterfaceImp.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        });

        custom.setTextColor("white");

        if (txt.getSender().getUsername().equals(currentClient.getClient().getUsername())) {

            custom.getHbox().setAlignment(Pos.CENTER_RIGHT);
            custom.getHbox().getChildren().addAll(vbox, circle);
            custom.getVbox().setStyle("-fx-background-radius :10;"
                    + "-fx-background-color : #00838e;");

        } else {
            custom.getHbox().getChildren().addAll(circle, vbox);
            custom.getHbox().setAlignment(Pos.CENTER_LEFT);
            custom.getVbox().setStyle("-fx-background-color : #9e9e9e;" + "-fx-background-radius :10;");
        }

        //ESPACE DU vbox
        custom.getHbox().setPadding(new Insets(10, 20, 10, 10));

        custom.getVbox().setMaxHeight(100);
        custom.getVbox().setMinHeight(100);

        custom.getVbox().setMaxWidth(80);
        custom.getVbox().setMinWidth(80);

        //then we add it to the chat 
        ChatContainer.getChildren().add(custom.getHbox());
    }

    public void dragScreen() {

        // Get the X and Y of the screen when pressed
        root.setOnMousePressed((MouseEvent event) -> {

            xOffset = event.getSceneX();
            yOffset = event.getSceneY();

        });

        // drag the screen depending ont the X and Y position 
        root.setOnMouseDragged((MouseEvent event) -> {

            Project.getStage().setX(event.getScreenX() - xOffset);
            Project.getStage().setY(event.getScreenY() - yOffset);

        });
    }

    @FXML
    private void disconnect(MouseEvent event) throws RemoteException {

        currentClient.getServer().disconnect(currentClient);

        System.exit(0);
    }

    @FXML
    private void sendMsg(MouseEvent event) throws RemoteException {

        if (!textMessage.getText().isEmpty()) {

            if (onlineUsers.getSelectionModel().getSelectedItem() != null) {

                //i update the server with the new message 
                currentClient.getServer().UpdateDisc(CurrentDiscussion, new TextMessage(currentClient.getClient(), textMessage.getText()));

                textMessage.setText("");

                //A GROUP
            } else if (groupsList.getSelectionModel().getSelectedItem() != null) {

                currentClient.getServer().UpdateDisc(new Discussion(groupsList.getSelectionModel().getSelectedItem().getMembres()), new TextMessage(currentClient.getClient(), textMessage.getText()));

                textMessage.setText("");

            } else {
                System.err.println("Select Somenone First");
            }
        }

        //this is the files part
        sendFile();

    }

    @FXML
    private void attachFile(MouseEvent event) {

        if (onlineUsers.getSelectionModel().getSelectedItem() != null) {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose a file");

            // this opens the file chooser and selects multiple files
            List<File> files = fileChooser.showOpenMultipleDialog(Project.getStage());

            //this is to populate list view of files
            if (files != null) {
                populateFilesList(files);
            }

        } else {
            System.err.println("Select Somenone First");

        }
    }

    @FXML
    private void videocall(MouseEvent event) throws IOException {

        // si les camera ne sont pas occupées -> appel en cours
        if (!currentClient.getServer().isCalling()) {

            videoCall = new VideoCall(CurrentMembers, currentClient.getClient());

            //si il y a une discu ouverte 
            if (onlineUsers.getSelectionModel().getSelectedItem() != null || groupsList.getSelectionModel().getSelectedItem() != null) {

                currentClient.getServer().makeCall(videoCall);

            } else {

                System.err.println("Select Someone First");
            }
        } else {

            System.err.println("Someone is already calling..");
        }
    }

    @FXML
    private void createGroup(MouseEvent event
    ) {

        System.err.println("Select from the online clients listview");

        onlineUsers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        go.setVisible(true);
    }

    @FXML
    private void groupSelected(MouseEvent event) throws RemoteException {

        if (onlineUsers.getSelectionModel().getSelectedItems().size() > 1) {

            ArrayList<Client> membres = new ArrayList<>();
            membres.add(currentClient.getClient());
            membres.addAll(onlineUsers.getSelectionModel().getSelectedItems());
            System.err.println(new Discussion(membres));
            currentClient.getServer().AddDisc(new Discussion(membres));
            onlineUsers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            go.setVisible(false);

        } else {
            System.err.println("Select more than one person using CTRL");
            go.setVisible(false);
        }
    }

    //SEARCH USERS BERK 
    private void search(String oldVal, String newVal) {

        if (oldVal != null && (newVal.length() < oldVal.length())) {
            populateConnectedUsers();
        }

        String[] parts = newVal.toUpperCase().split(" ");
        ObservableList<Client> subentries = FXCollections.observableArrayList();

        for (Client entry : onlineUsers.getItems()) {

            boolean match = true;

            String entryText = (String) entry.getUsername().toUpperCase();

            for (String part : parts) {

                if (!entryText.contains(part)) {
                    match = false;
                    break;
                }
            }

            if (match) {

                subentries.add(entry);
            }
        }
        onlineUsers.setItems(subentries);
    }

    //SEARCH GROUPS BERK 
    private void searchGroup(String oldVal, String newVal) {

        if (oldVal != null && (newVal.length() < oldVal.length())) {

            populategroups();
        }

        String[] parts = newVal.toUpperCase().split(" ");

        ObservableList<Discussion> subentries = FXCollections.observableArrayList();

        for (Discussion entry : groupsList.getItems()) {

            String value = "";
            boolean match = true;

            for (Client f : entry.getMembres()) {

                value += f.getUsername() + ",";

            }

            //boolean match = true;
            String entryText = (String) value.substring(0, value.length() - 1).toUpperCase();

            for (String part : parts) {

                if (!entryText.contains(part)) {
                    match = false;
                    break;
                }
            }

            if (match) {

                subentries.add(entry);
            }

        }
        groupsList.setItems(subentries);
    }

    private void sendFile() throws RemoteException {

        if (!filesList.getItems().isEmpty()) {

            ObservableList<File> files = filesList.getItems();

            for (File file : files) {

                //here i ll send the file(s) to server
                currentClient.getServer().sendFile(file, currentClient.getClient(), CurrentDiscussion);

            }

            //after i send to server i clear the listView
            filesList.getItems().clear();

        }

        if (VboxContainer.getChildren().size() == 2) {
            VboxContainer.getChildren().remove(1);
        }

    }

    @FXML
    private void changePicture(MouseEvent event) throws RemoteException, URISyntaxException, MalformedURLException, FileNotFoundException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an image");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpeg", "*.jpg", "*.png")
        );

        // this opens the file chooser and selects an image
        File file = fileChooser.showOpenDialog(Project.getStage());

        //if he selected a file
        if (file != null) {

            // here we save the file and change the path in BD
            String path = currentClient.getServer().changeProfilePic(currentClient, file);

            //here we just change the profile pic according to path -> change the initilizer
            Image im = new Image(new FileInputStream(path));

            profilePic.setFill(new ImagePattern(im));

            if (CurrentDiscussion != null) {
                currentClient.getServer().UpdateDisc(CurrentDiscussion, null);
            }

        }

    }

}
