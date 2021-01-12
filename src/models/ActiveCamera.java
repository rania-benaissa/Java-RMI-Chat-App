package models;

import com.github.sarxos.webcam.Webcam;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javax.imageio.ImageIO;

public class ActiveCamera extends Thread {

    private Webcam webcam;

    private ImageView image;

    private Image img;

    private int width;

    private int height;

    private GridPane grid;

    private boolean active;

    private int myPort;

    private int receiverPort;

    private InetAddress receiverAddress;

    private boolean receiverActive;

    private DatagramSocket ds;

    public ActiveCamera(GridPane grid, int value) throws UnknownHostException, SocketException {

        this.grid = grid;

        //get the width and height of the taken screen
        width = (int) (grid.getPrefWidth() / grid.getColumnConstraints().size());

        height = (int) (grid.getPrefHeight() / grid.getRowConstraints().size());

        // we choose the webcam -> check if there s two (otherwise check your pc)
        webcam = Webcam.getWebcamByName(Webcam.getWebcams().get(value).getName());

        // this stops the thread
        active = true;

        //this stop/activate the images transfer
        receiverActive = false;

        //if i m the caller
        if (value == 0) {

            myPort = 1234;
            receiverPort = 5678;
            receiverAddress = InetAddress.getByName("127.0.0.2");

        } else {
            // if i m the call receiver 
            myPort = 5678;
            receiverPort = 1234;
            receiverAddress = InetAddress.getByName("127.0.0.1");

        }

        ds = new DatagramSocket(myPort);

    }

    @Override
    public void run() {

        webcam.open();

        while (active) {

            try {

                Platform.runLater(
                        () -> {
                            if (webcam.isOpen()) {

                                grid.getChildren().clear();

                                BufferedImage bi = webcam.getImage();

                                img = SwingFXUtils.toFXImage(bi, null);

                                image = new ImageView(img);

                                image.setFitWidth(width);
                                image.setFitHeight(height);

                                grid.add(image, 0, 1);

                                if (receiverActive) {

                                    try {
                                        sendUDPmessage(bi);

                                        BufferedImage received = receiveUDPmessage();

                                        if (received != null) {

                                            ImageView image1;

                                            Image img1;

                                            img1 = SwingFXUtils.toFXImage(received, null);

                                            image1 = new ImageView(img1);

                                            image1.setFitWidth(width);
                                            image1.setFitHeight(height);

                                            // we display the received images there
                                            grid.add(image1, 0, 0);
                                        }
                                    } catch (IOException ex) {
                                        Logger.getLogger(ActiveCamera.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }
                            }
                        });

                Thread.sleep(150);

            } catch (InterruptedException ex) {
                Logger.getLogger(ActiveCamera.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public BufferedImage receiveUDPmessage() throws IOException {

        if (!ds.isClosed()) {

            try {

                DatagramPacket dp;

                /* wait for a p3 message */
                byte[] buf = new byte[50000];

                dp = new DatagramPacket(buf, buf.length);

                if (!ds.isClosed()) {

                    ds.receive(dp);

                    ByteArrayInputStream bais = new ByteArrayInputStream(dp.getData());

                    /*converts Byte array to buffered image*/
                    return ImageIO.read(bais);
                }

            } catch (SocketException ex) {
                Logger.getLogger(ActiveCamera.class
                        .getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ActiveCamera.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;

    }

    public void sendUDPmessage(BufferedImage message) {

        try {

            DatagramPacket dp;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ImageIO.write(message, "jpg", baos);

            baos.flush();

            byte[] buffer = baos.toByteArray();

            /* SEND A MESSAGE TO SERVER*/
            dp = new DatagramPacket(buffer, buffer.length, receiverAddress, receiverPort);

            if (!ds.isClosed()) {

                ds.send(dp);
            }

        } catch (SocketException ex) {
            Logger.getLogger(ActiveCamera.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ActiveCamera.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    public boolean isReceiverActive() {
        return receiverActive;
    }

    public void setReceiverActive(boolean receiverActive) {
        this.receiverActive = receiverActive;
    }

    public void closeCam() {

        ds.close();
        webcam.close();

    }

}
