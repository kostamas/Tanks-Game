/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tanks;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import static javax.swing.Spring.height;

public class Walls extends Pane {

    static final int[][] walls = {
        //left wall
        {0, 0}, {0, 50}, {0, 100}, {0, 150}, {0, 200}, {0, 250}, {0, 300}, {0, 350}, {0, 400}, {0, 450},
        //bottom wall
        {0, 350}, {50, 350}, {100, 350}, {150, 350}, {200, 350}, {250, 350}, {300, 350}, {350, 350}, {400, 350}, {450, 350},
        {500, 350}, {500, 350}, {550, 350}, {600, 350}, {650, 350}, {700, 350}, {750, 350}, 
        // right wall
        {750, 0}, {750, 50}, {750, 100}, {750, 150}, {750, 200}, {750, 250},
        {750, 300}, {750, 350}, {750, 400}, {750, 450},
        // top wall
        {0, 0}, {50, 0}, {100, 0}, {150, 0}, {200, 0}, {250, 0}, {300, 0}, {350, 0}, {400, 0}, {450, 0},
        {500, 0}, {500, 0}, {550, 0}, {600, 0}, {650, 0}, {700, 0}, {750, 0}
        // inner walls
    };
    static int length;
    String imgPath;

    public Walls(int length, String imgPath) {
        this.length = length;
        this.imgPath = imgPath;
        drawWall();
    }

    public int length() {
        return length;
    }

    public void drawWall() {
        for (int i = 0; i < this.walls.length; i++) {
            ImageView image = new ImageView(new javafx.scene.image.Image(imgPath));
            TilePane tile = new TilePane();
            tile.getChildren().add(image);
            tile.setTranslateX(this.walls[i][0]);
            tile.setTranslateY(this.walls[i][1]);
            getChildren().add(tile);
        }
    }

    public static boolean collision(int x, int y, int itemLength) {
        for (int i = 0; i < walls.length; i++) {
            boolean XCollision = (x + itemLength >= walls[i][0] && x < walls[i][0] + Walls.length);
            boolean YCollision = (y + itemLength >= walls[i][1] && y < walls[i][1] + Walls.length);
            if (XCollision && YCollision) {
                return true;
            }
        }
        return false;
    }
}
