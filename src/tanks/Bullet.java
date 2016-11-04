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
    int x, y, nextX, nextY, bulletLength;
    final int animationDuration = 45;
    int count, cycleCount;
    Timeline timeline;
    Tank shootingTank;
    StackPane root;  // the main pain 'injected' here because the shooting animation need the control to 
    // add/remove the bullet pane from the main pane.

    public Bullet(String imgPath, Tank shootingTank, int cycleCount, StackPane root, int bulletLength) {
        this.cycleCount = cycleCount;
        this.imgPath = imgPath;
        this.count = 0;
        this.root = root;
        this.bulletLength = bulletLength;
        root.getChildren().add(this);
        this.shootingTank = shootingTank;
    }

    public void fly(int x, int y, int nextX, int nextY, StackPane root, int power) {
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

        timeline = new Timeline(new KeyFrame(Duration.millis(this.animationDuration), keyFrameFn -> animate(power)));
        timeline.setCycleCount(cycleCount);

        timeline.play();
    }

    private KeyFrame animate(int power) {
        count++;
        getChildren().remove(image); // remove bullet image from the bullet pane.
        x += nextX;
        y += nextY;

        for (int i = 0; i < Walls.walls.length; i++) {
            boolean XCollision = (x + this.bulletLength >= Walls.walls[i][0] && x < Walls.walls[i][0] + Walls.length);
            boolean YCollision = (y + this.bulletLength >= Walls.walls[i][1] && y < Walls.walls[i][1] + Walls.length);
            if (XCollision && YCollision) {
                timeline.stop();
                return null;

            }
        }
        setTranslateX(x);
        setTranslateY(y);
        image = new ImageView(new javafx.scene.image.Image(imgPath));
        getChildren().add(image);
        boolean wasHit = GameStatus.checkIfTankeHit(x, y, this.bulletLength, this.shootingTank, power);
        if (count >= cycleCount || wasHit) {
            timeline.stop();
            getChildren().remove(image);
            root.getChildren().remove(this);
            count = 0;
        }
        return null;
    }
}
