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
import javafx.scene.layout.StackPane;

public class Tank extends Pane {

    ImageView image;
    int[] currentPosition;
    int currentDirection;
    int numOfDirections;
    int tankLentgh;
    String[] imagesDirections;
    String imagesPrefixPath;

    Tank(int x, int y, int initilizedDirection, int tankLentgh, String initilizedImagePath, String[] imagesDirections, String imagesPrefixPath) {
        currentPosition = new int[2];
        setPosition(x, y, initilizedImagePath);
        this.currentDirection = initilizedDirection;
        this.imagesDirections = imagesDirections;
        this.imagesPrefixPath = imagesPrefixPath;
        this.tankLentgh = tankLentgh;
    }

    public void move(int x, int y, String imgPath) {
        int [][]walls = Walls.walls;
        int wallLength = Walls.length;
        
        for(int i = 0 ; i < walls.length; i++){
            boolean XCollision = (x + this.tankLentgh >= walls[i][0] && x < walls[i][0] + wallLength);
            boolean YCollision = (y + this.tankLentgh >= walls[i][1] && y < walls[i][1] + wallLength);
            if(XCollision && YCollision){
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

    public void shot(int x, int y, int nextX, int nextY, StackPane root) {
        Bullet bullet = new Bullet("assets/green_bullet.png", 16, root);
        
        bullet.fly(x, y, nextX, nextY, root);
    }
}
