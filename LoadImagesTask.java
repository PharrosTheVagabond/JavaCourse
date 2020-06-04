import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LoadImagesTask extends Task<Void> {

    private List<File> listFromChooser;
    private Pane pane;
    private double imageFitWidth;
    private double imageFitHeight;

    public LoadImagesTask(List<File> listFromChooser, Pane pane, double imageFitWidth, double imageFitHeight) {
        this.listFromChooser = listFromChooser;
        this.pane = pane;
        this.imageFitWidth = imageFitWidth;
        this.imageFitHeight = imageFitHeight;
    }

    @Override
    protected Void call() {
        int i = 0;
        for (File file : listFromChooser) {
            updateMessage("Loading " + file.getName() + ".");
            CustomImageView customImageView = new CustomImageView();
            try {
                customImageView.setImage(file, imageFitWidth, imageFitHeight);
            }
            catch (IOException exception) {
                continue;
            }
            Platform.runLater(() -> pane.getChildren().add(customImageView));
            updateProgress(++i, listFromChooser.size());
            if (isCancelled()) {
                break;
            }
        }
        updateMessage("Loaded " + i + " image(s).");
        return null;
    }
}
