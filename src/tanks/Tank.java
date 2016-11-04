/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tanks;

import java.awt.Image;
import java.awt.Rectangle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import static javafx.application.ConditionalFeature.FXML;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Tank extends Pane {

    ImageView image, hitImage;
    int life = TankConst.TANK_LIFE;
    int[] currentPosition;
    int currentDirection;
    int numOfDirections;
    String imagesPrefixPath;
    int[] startPosition;
    StackPane root;

    Tank(int x, int y, int initilizedDirection, String initilizedImagePath, String imagesPrefixPath, StackPane root) {
        currentPosition = new int[2];
        startPosition = new int[2];
        startPosition[0] = x;
        startPosition[1] = y;
        setPosition(x, y, initilizedImagePath);
        this.currentDirection = initilizedDirection;
        this.imagesPrefixPath = imagesPrefixPath;
        this.root = root;
    }

    public int[] getCurrentPosition() {
        return this.currentPosition;
    }


    public int[] getStartedPosition() {
        return this.startPosition;
    }

    public void setStartedPostion(int x, int y) {
        startPosition[0] = x;
        startPosition[1] = y;
    }

    int getDirection() {
        return this.currentDirection;
    }

    public int getLife() {
        return this.life;
    }

    public void move(int x, int y, String imgPath) {
        int[][] walls = Walls.walls;
        int wallLength = Walls.length;

        for (int i = 0; i < walls.length; i++) {
            boolean XCollision = (x + TankConst.tankLength >= walls[i][0] && x < walls[i][0] + wallLength);
            boolean YCollision = (y + TankConst.tankLength >= walls[i][1] && y < walls[i][1] + wallLength);
            if (XCollision && YCollision) {
                return;
            }
        }
        setPosition(x, y, imgPath);
    }

    public void turnRight() {
        this.currentDirection = (this.currentDirection + 1) % TankConst.directions.length;
        String nextImage = this.imagesPrefixPath + TankConst.directions[this.currentDirection] + ".png";
        setPosition(this.currentPosition[0], this.currentPosition[1], nextImage);
    }

    public void turnLeft() {
        this.currentDirection = this.currentDirection > 0 ? this.currentDirection - 1 : TankConst.directions.length - 1;
        String nextImage = this.imagesPrefixPath + TankConst.directions[this.currentDirection] + ".png";
        setPosition(this.currentPosition[0], this.currentPosition[1], nextImage);
    }

    public void setPosition(int x, int y, String imgPath) {
        getChildren().remove(image);
        image = new ImageView(new javafx.scene.image.Image(imgPath));
        getChildren().add(image);
        setTranslateX(x);
        setTranslateY(y);
        this.currentPosition[0] = x;
        this.currentPosition[1] = y;
    }

    public void shot(StackPane root, Tank shootingTank) {
        int direction = shootingTank.getDirection();
        int x = shootingTank.getCurrentPosition()[0] + TankConst.bulletOffsetByDirection[direction][0];
        int y = shootingTank.getCurrentPosition()[1] + TankConst.bulletOffsetByDirection[direction][1];
        int nextX = 0, nextY = 0;

        if (direction != 0 && direction != 4) {
            nextX += (direction == 1 || direction == 2 || direction == 3) ? 10 : -10;
        }

        if (direction != 2 && direction != 6) {
            nextY += (direction == 0 || direction == 1 || direction == 7) ? -10 : 10;
        }
        Bullet bullet = new Bullet("assets/green_bullet.png", this, 16, root, 10);
        bullet.fly(x, y, nextX, nextY, root);
    }

    public void updateLife(int newLife) {
        this.life = newLife;
    }

    public void hitted() {
        if (this.getLife() > 0) {
            updateLife(this.getLife() - 1);
        }

        ImageView hitImage = new ImageView("assets/tank_hit.png");
        getChildren().add(hitImage);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(150), keyFrameFn -> animate(hitImage)));
        timeline.play();
         if (this.getLife() <= 0) {
            root.getChildren().remove(this);
        }
    }

    private KeyFrame animate(ImageView hitImage) {
        getChildren().remove(hitImage);
        return null;
    }
}
