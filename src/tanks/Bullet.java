package tanks;

import java.awt.Image;
import java.awt.Rectangle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import static javafx.application.ConditionalFeature.FXML;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Bullet extends Pane {

    ImageView image;
    String imgPath;
    int interval;
    int x, y, nextX, nextY;
    final int animationDuration = 45;
    int count, cycleCount;
    Timeline timeline;
    StackPane root;  // the main pain 'injected' here because the shooting animation need the control to 
    // add/remove the bullet pane from the main pane.

    public Bullet(String imgPath, int cycleCount, StackPane root) {
        this.cycleCount = cycleCount;
        this.imgPath = imgPath;
        this.count = 0;
        this.root = root;
        root.getChildren().add(this);
    }

    public void fly(int x, int y, int nextX, int nextY, StackPane root) {
        if (timeline != null && (timeline.getStatus().compareTo(Animation.Status.STOPPED) != 0)) {
            return;
        }
        setTranslateX(x);
        setTranslateY(y);
        image = new ImageView(new javafx.scene.image.Image(imgPath));
        getChildren().add(image);

        this.x = x;
        this.y = y;
        this.nextX = nextX;
        this.nextY = nextY;

        timeline = new Timeline(new KeyFrame(Duration.millis(this.animationDuration), keyFrameFn -> animate()));
        timeline.setCycleCount(cycleCount);

        timeline.play();
    }

    private KeyFrame animate() {
        count++;
        getChildren().remove(image); // remove bullet image from the bullet pane.
        x += nextX;
        y += nextY;
        System.out.println("X:" + getTranslateX() + "  Y:" + getTranslateY());
        setTranslateX(x);
        setTranslateY(y);
        image = new ImageView(new javafx.scene.image.Image(imgPath));
        getChildren().add(image);
        if (count >= cycleCount) {
            getChildren().remove(image);
            root.getChildren().remove(this);  // remove
            count = 0;
        }
        return null;
    }
}
