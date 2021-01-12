/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import animatefx.animation.FadeInRight;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import models.Client;
import models.ClientInterfaceImp;
import views.Project;

/**
 * FXML Controller class
 *
 * @author Rania
 */
public class LoginController implements Initializable {

    private double xOffset = 0;
    private double yOffset = 0;

    private ClientInterfaceImp currentClient;

    @FXML
    private AnchorPane root;
    @FXML
    private Pane contentPane;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // make the scene draggable
        dragScreen();

        // get the current client
        currentClient = Project.getClient();

        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Username can't be empty");

        username.getValidators().add(validator);

        username.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                username.validate();
            }
        });

        validator = new RequiredFieldValidator();

        validator.setMessage("Password can't be empty");

        password.getValidators().add(validator);

        password.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                password.validate();
            }
        });

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
    private void login(MouseEvent event) throws RemoteException, IOException {

        if (!username.getText().isEmpty() && !password.getText().isEmpty()) {

            currentClient.setClient(new Client(username.getText(), password.getText()));

            int code = currentClient.getServer().CheckClientInfos(currentClient.getClient(), 1);

            switch (code) {
                case 0:

                    // this makes the client notify its connection to the server
                    currentClient.getServer().connectClient(currentClient, currentClient.getClient());

                    //getting the name from the server
                    currentClient.setClient(currentClient.getServer().getClient(currentClient));
                    //i added this
                    Project.setClient(currentClient);
                    
                    // change the root to chatRoom one
                    Parent parent = FXMLLoader.load(getClass().getResource("/views/ChatRoom.fxml"));
                    Project.getStage().setScene(new Scene(parent));
                    break;
                case 1:
                    System.err.println("Account doesnt exist");
                    break;
                case 2:
                    System.err.println("Wrong password");
                    break;
                default:
                    System.err.println("Error");
                    break;
            }

        }

    }

    @FXML
    private void openRegistration(MouseEvent event) throws IOException {
        // get the resource
        Parent register = FXMLLoader.load(getClass().getResource("/views/Registration.fxml"));

        // add animation
        new FadeInRight(register).play();

        // set the registration instead of login
        contentPane.getChildren().removeAll();
        contentPane.getChildren().setAll(register);
    }

    @FXML
    private void close_app(MouseEvent event) {
        // close the app
        System.exit(0);
    }

}
