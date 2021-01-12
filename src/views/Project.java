package views;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.ClientInterfaceImp;

/**
 *
 * @author Rania
 */
public class Project extends Application {

    private static Stage stage;

    private static ClientInterfaceImp client;

    @Override
    public void start(Stage stage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene scene = new Scene(root);

        // keep the stage value for other uses
        Project.stage = stage;

        // make the stage have a transparent background 
        stage.initStyle(StageStyle.TRANSPARENT);

        stage.setScene(scene);

        Platform.setImplicitExit(false);

        Platform.runLater(() -> {
            stage.show();
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {

        client = new ClientInterfaceImp();
        launch(args);
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        Project.stage = stage;
    }

    public static ClientInterfaceImp getClient() {
        return client;
    }

    public static void setClient(ClientInterfaceImp client) {
        Project.client = client;
    }

}
