/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tanks;

import java.awt.Image;
import java.awt.Rectangle;
import static javafx.application.ConditionalFeature.FXML;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Tank extends Pane {

    ImageView image;
    int[] currentPosition;
    int currentDirection;
    int numOfDirections;
    String[] imagesDirections;
    String imagesPrefixPath;
    Bullet bullet;

    Tank(int x, int y, int initilizedDirection, String initilizedImagePath, String[] imagesDirections, String imagesPrefixPath, Bullet bullet) {
        currentPosition = new int[2];
        setPosition(x, y, initilizedImagePath);
        this.currentDirection = initilizedDirection;
        this.imagesDirections = imagesDirections;
        this.imagesPrefixPath = imagesPrefixPath;
        this.bullet = bullet;
    }

    public void move(int x, int y, String imgPath) {
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

    public void shot(int x, int y, int nextX, int nextY) {
        this.bullet.fly(x, y, nextX, nextY);
    }
}
