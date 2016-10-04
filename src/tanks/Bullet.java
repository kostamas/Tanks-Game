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
import javafx.util.Duration;

public class Bullet extends Pane {

    ImageView image;
    String imgPath;
    int interval;
    int x, y, nextX, nextY;
    int count, cycleCount;
    Timeline timeline;

    public Bullet(String imgPath, int cycleCount) {
        this.cycleCount = cycleCount;
        this.imgPath = imgPath;
        this.count = 0;
    }

    public void fly(int x, int y, int nextX, int nextY) {
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

        timeline = new Timeline(new KeyFrame(
                Duration.millis(45),
                keyFrameFn -> animate()));
        timeline.setCycleCount(cycleCount);

        timeline.play();
    }

    private KeyFrame animate() {
        count++;
        getChildren().remove(image);
        x += nextX;
        y += nextY;
        System.out.println("X:" + getTranslateX() + "  Y:" + getTranslateY());
        setTranslateX(x);
        setTranslateY(y);
        image = new ImageView(new javafx.scene.image.Image(imgPath));
        getChildren().add(image);
        if (count >= cycleCount) {
            getChildren().remove(image);
            count = 0;
        }
        return null;
    }
}
