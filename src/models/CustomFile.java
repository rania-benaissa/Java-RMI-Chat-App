package models;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Rania
 */
public class CustomFile implements Serializable {

    private HBox hbox;

    private VBox vbox;

    private Label label;

    private File file;

    private ImageView image;

    public CustomFile(File file, String name) {

        hbox = new HBox();

        vbox = new VBox();

        label = new Label();

        //save the file
        this.file = file;

        create(name);

    }

    private void create(String name) {

        label.setText(name);

        // create the card View to show
        vbox.setAlignment(Pos.CENTER);

        vbox.setSpacing(20);
        vbox.setMinWidth(60);
        vbox.setMaxWidth(60);

        // here we get the file's icon
        ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file);
        java.awt.Image awtIm = icon.getImage();

        // here we convert it to an FX image
        Image img = SwingFXUtils.toFXImage((BufferedImage) awtIm, null);
        image = new ImageView(img);
        // Add the values  to the HBox

        vbox.getChildren()
                .addAll(
                        image,
                        label
                );

        hbox.getChildren().add(vbox);

        vbox.setCursor(Cursor.HAND);

        hbox.setAlignment(Pos.TOP_RIGHT);

        hbox.setSpacing(10);

    }

    public HBox getHbox() {
        return hbox;
    }

    public File getFile() {
        return file;
    }

    public VBox getVbox() {
        return vbox;
    }

    public void setVbox(VBox vbox) {
        this.vbox = vbox;
        hbox.getChildren().removeAll();
        hbox.getChildren().add(vbox);

    }

    public void setTextColor(String color) {

        label.setTextFill(Paint.valueOf(color));

    }

}
