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
        {0, 0}, {0, 50}, {0, 100}, {0, 150}, {0, 200}, {0, 250}, {0, 300}, {0, 350}, {0, 400}, {0, 450}, {0, 500}, {0, 550},
        //bottom wall
        {0, 550}, {50, 550}, {100, 550}, {150, 550}, {200, 550}, {250, 550}, {300, 550}, {350, 550}, {400, 550}, {450, 550},
        {500, 550}, {500, 550}, {550, 550}, {600, 550}, {650, 550}, {700, 550}, {750, 550}, {800, 550}, {850, 550},
        {900, 550}, {950, 550}, {1000, 550}, {1050, 550}, {1100, 550}, {1150, 550}, {1200, 550}, {1250, 550},
        // right wall
        {1250, 0}, {1250, 50}, {1250, 100}, {1250, 150}, {1250, 200}, {1250, 250},
        {1250, 300}, {1250, 350}, {1250, 400}, {1250, 450}, {1250, 500}, {1250, 550},
        // top wall
        {0, 0}, {50, 0}, {100, 0}, {150, 0}, {200, 0}, {250, 0}, {300, 0}, {350, 0}, {400, 0}, {450, 0},
        {500, 0}, {500, 0}, {550, 0}, {600, 0}, {650, 0}, {700, 0}, {750, 0}, {800, 0}, {850, 0},
        {900, 0}, {950, 0}, {1000, 0}, {1050, 0}, {1100, 0}, {1150, 0}, {1200, 0}, {1250, 0}
        // inner walls

//        {200, 450}, {250, 450}, {300, 450}, {350, 450},
//        {300, 100}, {300, 150}, {300, 200}, {300, 2500},
//        {750, 300}, {750, 350}, {750, 400}, {750, 450}, {800, 350}, {850, 350},
//        {900, 200}, {950, 200}, {1000, 200}, {1050, 200}, {1100, 200}
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
