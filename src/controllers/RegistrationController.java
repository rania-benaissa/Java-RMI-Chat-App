/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import models.Client;
import models.ClientInterfaceImp;
import views.Project;

/**
 * FXML Controller class
 *
 * @author Rania
 */
public class RegistrationController implements Initializable {

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private JFXTextField name;
    @FXML
    private JFXTextField surname;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;

    private ClientInterfaceImp currentClient;
    @FXML
    private AnchorPane root;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        dragScreen();

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

        validator = new RequiredFieldValidator();
        validator.setMessage("Name can't be empty");
        name.getValidators().add(validator);
        name.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                name.validate();
            }
        });
        validator = new RequiredFieldValidator();
        validator.setMessage("Surname can't be empty");
        surname.getValidators().add(validator);
        surname.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                surname.validate();
            }
        });

    }

    @FXML
    private void register(MouseEvent event) throws RemoteException, IOException {

        if (!username.getText().isEmpty() && !password.getText().isEmpty() && !name.getText().isEmpty() && !surname.getText().isEmpty()) {

            currentClient.setClient(new Client(username.getText(), password.getText(), name.getText(), surname.getText()));

            //if client doesnt exist already, creates and connects him
            int code = currentClient.getServer().CheckClientInfos(currentClient.getClient(), 2);

            switch (code) {
                case 4:
                    // this makes the client notify its connection to the server
                    currentClient.getServer().connectClient(currentClient, currentClient.getClient());
                    // change the root to chatRoom one
                    Parent root = FXMLLoader.load(getClass().getResource("/views/ChatRoom.fxml"));
                    Project.getStage().setScene(new Scene(root));

                    break;
                case 3:
                    System.err.println("An account with this username already exists");
                    break;
                default:
                    System.err.println("Error");
                    break;
            }

        }

    }

    @FXML
    private void close_app(MouseEvent event) {
        // close the app
        Platform.exit();
        System.exit(0);

    }

    @FXML
    private void backToLogin(MouseEvent event) throws IOException {
        //Go back to login 
        Parent root = FXMLLoader.load(getClass().getResource("/views/Login.fxml"));
        Project.getStage().getScene().setRoot(root);
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

}
