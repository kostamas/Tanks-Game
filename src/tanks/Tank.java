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
    int life = 5;
    int[] currentPosition;
    int currentDirection;
    int numOfDirections;
    int tankLentgh;
    String tankName;
    String[] imagesDirections;
    String imagesPrefixPath;
    int[][] bulletOffsetByDirection = {{10, 0}, {30, 0}, {37, 11}, {35, 35}, {10, 35}, {-5, 35}, {0, 11}, {0, 0}};

    Tank(int x, int y, int initilizedDirection, int tankLentgh, String initilizedImagePath, String[] imagesDirections, String imagesPrefixPath, String tankName) {
        currentPosition = new int[2];
        setPosition(x, y, initilizedImagePath);
        this.currentDirection = initilizedDirection;
        this.imagesDirections = imagesDirections;
        this.imagesPrefixPath = imagesPrefixPath;
        this.tankLentgh = tankLentgh;
        this.tankName = tankName;
    }

    public void move(int x, int y, String imgPath) {
        int[][] walls = Walls.walls;
        int wallLength = Walls.length;

        for (int i = 0; i < walls.length; i++) {
            boolean XCollision = (x + this.tankLentgh >= walls[i][0] && x < walls[i][0] + wallLength);
            boolean YCollision = (y + this.tankLentgh >= walls[i][1] && y < walls[i][1] + wallLength);
            if (XCollision && YCollision) {
                return;
            }
        }
        setPosition(x, y, imgPath);
    }

    public void turnRight() {
        this.currentDirection = (this.currentDirection + 1) % imagesDirections.length;
        String nextImage = this.imagesPrefixPath + imagesDirections[this.currentDirection] + ".png";
        setPosition(this.currentPosition[0], this.currentPosition[1], nextImage);
    }

    public void turnLeft() {
        this.currentDirection = this.currentDirection > 0 ? this.currentDirection - 1 : imagesDirections.length - 1;
        String nextImage = this.imagesPrefixPath + imagesDirections[this.currentDirection] + ".png";
        setPosition(this.currentPosition[0], this.currentPosition[1], nextImage);
    }

    int getDirection() {
        return this.currentDirection;
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

    public int[] getCurrentPosition() {
        return this.currentPosition;
    }
    
    public String getTankName() {
        return this.tankName;
    }

    public void shot(StackPane root, Tank shootingTank) {
        int direction = shootingTank.getDirection();
        int x = shootingTank.getCurrentPosition()[0] + bulletOffsetByDirection[direction][0];
        int y = shootingTank.getCurrentPosition()[1] + bulletOffsetByDirection[direction][1];
        int nextX = 0, nextY = 0;

        if (direction != 0 && direction != 4) {
            nextX += (direction == 1 || direction == 2 || direction == 3) ? 10 : -10;
        }

        if (direction != 2 && direction != 6) {
            nextY += (direction == 0 || direction == 1 || direction == 7) ? -10 : 10;
        }
        Bullet bullet = new Bullet("assets/green_bullet.png",this, 16, root, 10);
        bullet.fly(x, y, nextX, nextY, root);
    }

    public int getLife() {
        return this.life;
    }

    public void updateLife(int newLife) {
        this.life = newLife;
    }

    public int getTankLength() {
        return this.tankLentgh;
    }

    public void hitted() {
        if (this.getLife() > 0) {
            updateLife(this.getLife() - 1);
        }
        ImageView hitImage = new ImageView("assets/tank_hit.png");
        getChildren().add(hitImage);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(150), keyFrameFn -> animate(hitImage)));
        timeline.play();
    }

    private KeyFrame animate(ImageView hitImage) {
        getChildren().remove(hitImage);
        return null;
    }
}
