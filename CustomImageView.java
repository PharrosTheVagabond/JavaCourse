import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomImageView extends AnchorPane {

    @FXML
    private ImageView imageView;
    @FXML
    private Button deleteButton;
    @FXML
    private Label infoLabel;
    private String name;
    private int pixels;
    private long fileSize;
    private Date lastModified;
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public CustomImageView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "CustomImageView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setImage(File file, double fitWidth, double fitHeight) throws IOException {
        name = file.getName();

        int width;
        int height;
        try (InputStream stream = new FileInputStream(file)) {
            try (ImageInputStream input = ImageIO.createImageInputStream(stream)) {
                ImageReader reader = ImageIO.getImageReaders(input).next();
                try {
                    reader.setInput(input);
                    width = reader.getWidth(0);
                    height = reader.getHeight(0);
                }
                finally {
                    reader.dispose();
                }
            }
        }

        Image image = new Image(file.toURI().toString(), fitWidth, fitHeight, true, true);

        imageView.setImage(image);
        fileSize = file.length();
        pixels = width * height;
        lastModified = new Date(file.lastModified());

        String sizeRounded;
        double megaBytes = fileSize / Math.pow(2, 20);
        double kiloBytes = fileSize / Math.pow(2, 10);
        if (megaBytes >= 1) {
            sizeRounded = String.format("%.2f", megaBytes) + " MB";
        }
        else if (kiloBytes >= 1) {
            sizeRounded = String.format("%.2f", kiloBytes) + " KB";
        }
        else {
            sizeRounded = fileSize + " B";
        }
        infoLabel.setText("Name: " + name + "\n" +
                "Definition: " + width + "x" + height + "\n" +
                "Size: " + sizeRounded + "\n" +
                "Last modified: " + dateFormat.format(file.lastModified()));
    }

    public Image getImage() { return imageView.getImage(); }

    public String getName() { return name; }

    public int getPixels() { return pixels; }

    public long getFileSize() { return fileSize; }

    public Date getLastModified() { return lastModified; }

    @FXML
    private void deleteFromParent() {
        ((Pane)getParent()).getChildren().remove(this);
    }

    @FXML
    private void onMouseEntered() {
        infoLabel.setVisible(true);
        deleteButton.setVisible(true);
    }

    @FXML
    private void onMouseExited() {
        infoLabel.setVisible(false);
        deleteButton.setVisible(false);
    }
}
