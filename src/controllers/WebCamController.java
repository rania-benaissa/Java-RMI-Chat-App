package controllers;

import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import models.ActiveCamera;
import models.Client;
import models.ClientInterfaceImp;
import models.VideoCall;
import views.Project;

/**
 * FXML Controller class
 *
 * @author Rania
 */
public class WebCamController implements Initializable {

    @FXML
    private GridPane grid;

    private static ActiveCamera myCam;

    private static VideoCall videoCall;
    @FXML
    private HBox callCommands;

    private ClientInterfaceImp currentClient;
    @FXML
    private Label textCall;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {

            //get the current client
            currentClient = Project.getClient();

            //if i m the sender
            if (videoCall.getSender().equals(currentClient.getClient())) {

                //create a cam, it takes my pc's webcam
                myCam = new ActiveCamera(grid, 0);

                //we remove the accept call button
                callCommands.getChildren().remove(0);

                String receivers = "";

                for (Client client : videoCall.getMembers()) {

                    if (!client.equals(videoCall.getSender())) {

                        receivers += " " + client.getSurname() + " " + client.getName();
                    }
                }

                textCall.setText("You are calling : " + receivers);

                // we run the cam
                myCam.start();

            } else {
                textCall.setText(videoCall.getSender().getSurname() + " " + videoCall.getSender().getName() + " : is calling you");

            }

        } catch (RemoteException | UnknownHostException | SocketException ex) {

            Logger.getLogger(WebCamController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void acceptCall(MouseEvent event) throws RemoteException, UnknownHostException, SocketException {

        // we re here only if someone is calling us == aka we re not senders
        //i give my phone's camera to this user
        myCam = new ActiveCamera(grid, 1);

        //add client to video call active members
        currentClient.getServer().addActiveMember(videoCall.getMembers(), currentClient.getClient());

        myCam.start();

        //we remove the accept call button
        callCommands.getChildren().remove(0);

    }

    @FXML
    private void rejectCall(MouseEvent event) throws RemoteException, IOException {

        //remove client from video call active members
        currentClient.getServer().removeActiveMember(videoCall.getMembers(), currentClient.getClient());

    }

    public static void setVideoCall(VideoCall videoCall) {

        WebCamController.videoCall = videoCall;

    }

    public static ActiveCamera getMyCam() {
        return myCam;
    }

}
