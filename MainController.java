import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {

    private final double IMAGE_COMPARING_WIDTH = 64;
    private final double IMAGE_COMPARING_HEIGHT = 64;
    @FXML
    private Button loadButton;
    @FXML
    private MenuButton sortMenuButton;
    @FXML
    private TilePane picturesPane;
    @FXML
    private Label bottomLabel;
    @FXML
    private ProgressBar progressBar;
    private FileChooser imagesLoadChooser = new FileChooser();
    private LinkedList<Task<Void>> runningTasks = new LinkedList<>();

    public MainController() {
        imagesLoadChooser.setTitle("Open Pictures");
        imagesLoadChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.jpeg", "*.png"));
    }

    @FXML
    private void loadButtonClicked() {
        List<File> openedFiles = imagesLoadChooser.showOpenMultipleDialog(loadButton.getScene().getWindow());
        if (openedFiles != null) {
            LoadImagesTask task = new LoadImagesTask(
                    openedFiles, picturesPane, picturesPane.getPrefTileWidth(), picturesPane.getPrefTileHeight());
            registerTaskTracking(task);
            Thread imagesLoadingThread = new Thread(task);
            imagesLoadingThread.start();
        }
    }

    private void registerTaskTracking(Task<Void> newTask) {
        runningTasks.add(newTask);
        sortMenuButton.setDisable(true);

        if (!progressBar.progressProperty().isBound()) {
            progressBar.setVisible(true);
            progressBar.progressProperty().bind(newTask.progressProperty());
            bottomLabel.textProperty().bind(newTask.messageProperty());
        }

        newTask.setOnSucceeded(e -> taskFinished(newTask));
        newTask.setOnCancelled(e -> taskFinished(newTask));
        newTask.setOnFailed(e -> taskFinished(newTask));
    }

    private void taskFinished(Task<Void> currentTask) {
        runningTasks.remove(currentTask);
        if (!runningTasks.isEmpty()) {
            progressBar.progressProperty().bind(runningTasks.get(0).progressProperty());
            bottomLabel.textProperty().bind(runningTasks.get(0).messageProperty());
        } else {
            progressBar.progressProperty().unbind();
            progressBar.setVisible(false);
            bottomLabel.textProperty().unbind();
            sortMenuButton.setDisable(false);
        }
    }

    @FXML
    private void sortByName() {
        sort(Comparator.comparing(CustomImageView::getName));
        bottomLabel.setText("Sorted by file name.");
    }

    @FXML
    private void sortByDimensions() {
        sort(Comparator.comparing(CustomImageView::getPixels));
        bottomLabel.setText("Sorted by dimensions (total size in pixels).");
    }

    @FXML
    private void sortByFileSize() {
        sort(Comparator.comparing(CustomImageView::getFileSize));
        bottomLabel.setText("Sorted by file size.");
    }

    @FXML
    private void sortByLastModified() {
        sort(Comparator.comparing(CustomImageView::getLastModified));
        bottomLabel.setText("Sorted by last modified date.");
    }

    @FXML
    private void sortBySimilarity() {
        bottomLabel.setText("Click on the image to compare with (RMB to cancel). " +
                "The sorting process might take some time.");
        picturesPane.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    try {
                        ImageView targetImageView = (ImageView) mouseEvent.getTarget();
                        picturesPane.removeEventHandler(mouseEvent.getEventType(), this);
                        String name = ((CustomImageView)targetImageView.getParent()).getName();

                        Image resizedPrimaryImage = resizeImage(targetImageView.getImage(),
                                IMAGE_COMPARING_WIDTH, IMAGE_COMPARING_HEIGHT, false, true);
                        sort((o1, o2) -> (int) (imagesDistance(resizedPrimaryImage, o1.getImage()) -
                                                imagesDistance(resizedPrimaryImage, o2.getImage())));
                        if (!bottomLabel.textProperty().isBound()) {
                            bottomLabel.setText("Sorted by similarity to " + name + ".");
                        }
                    } catch (ClassCastException ignored) {
                    }
                } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    picturesPane.removeEventHandler(mouseEvent.getEventType(), this);
                    bottomLabel.setText("");
                }
            }
        });
    }

    private Image resizeImage(Image image, double width, double height, boolean preserveRatio, boolean smooth) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(preserveRatio);
        imageView.setSmooth(smooth);
        return imageView.snapshot(null, null);
    }

    private double imagesDistance(Image resizedFirstImage, Image secondImage) {
        double value = 0;
        double width = resizedFirstImage.getWidth();
        double height = resizedFirstImage.getHeight();
        Image resizedSecondImage = resizeImage(secondImage, width, height, false, true);
        PixelReader pixelReader1 = resizedFirstImage.getPixelReader();
        PixelReader pixelReader2 = resizedSecondImage.getPixelReader();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color1 = pixelReader1.getColor(x, y);
                Color color2 = pixelReader2.getColor(x, y);
                value += Math.abs(HsbArbitraryValue(color1) - HsbArbitraryValue(color2));
            }
        }
        return value;
    }

    private double HsbArbitraryValue(Color color) {
        return (Math.cos(Math.toRadians(color.getHue())) + Math.sin(Math.toRadians(color.getHue()))) *
                180 + color.getBrightness() + color.getSaturation();
    }

    private void sort(Comparator<CustomImageView> comparator) {
        picturesPane.getChildren().setAll(picturesPane.getChildren()
                .stream()
                .map(e -> (CustomImageView) e)
                .sorted(comparator)
                .collect(Collectors.toList()));
    }
}