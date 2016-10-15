/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tanks;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GameStatus {

    static Tank[] tanks = new Tank[2];
    static Label textTank1;
    static Label textTank2;
    static StackPane root;
    static boolean gameFinished = false;
    static String[] directions = {"up", "up_right", "right", "down_right", "down", "down_left", "left", "up_left"};

    public GameStatus(Tank tank1, Tank tank2, StackPane root) {
        GameStatus.tanks[0] = tank1;
        GameStatus.tanks[1] = tank2;
        this.root = root;
        updateText();
    }

    public static void updateText() {
        root.getChildren().remove(textTank1);
        root.getChildren().remove(textTank2);

        textTank1 = new Label("YOUR TANK LIFE: " + GameStatus.tanks[0].getLife());
        textTank2 = new Label("COMPUTER TANK LIFE: " + GameStatus.tanks[1].getLife());

        textTank1.setTextFill(Color.web("#fbfbfb"));
        textTank1.setFont(new Font(20));
        textTank1.setTranslateX(-510);
        textTank1.setTranslateY(-275);

        textTank2.setTextFill(Color.web("#fbfbfb"));
        textTank2.setFont(new Font(20));
        textTank2.setTranslateX(480);
        textTank2.setTranslateY(-275);

        root.getChildren().add(textTank1);
        root.getChildren().add(textTank2);
    }

    public static boolean checkIfTankeHit(int bulletXPosition, int bulletYPosition, int bulletLength, Tank shootingTank) {
        for (int i = 0; i < GameStatus.tanks.length; i++) {
            int tankXPosition = GameStatus.tanks[i].getCurrentPosition()[0];
            int tankYPosition = GameStatus.tanks[i].getCurrentPosition()[1];
            boolean XCollision = (bulletXPosition >= tankXPosition && bulletXPosition < tankXPosition + tanks[i].getTankLength());
            boolean YCollision = (bulletYPosition >= tankYPosition && bulletYPosition < tankYPosition + tanks[i].getTankLength());
            if (XCollision && YCollision && !shootingTank.getTankName().equals(GameStatus.tanks[i].getTankName())) {
                handleTankHit(GameStatus.tanks[i], i);
                return true;
            }
        }
        return false;
    }

    public static void handleTankHit(Tank tank, int tankId) {
        tank.hitted();
        updateText();

        if (tank.getLife() == 0 && !gameFinished) {
            showHowWon(tankId);
            gameFinished = true;
        }
    }

    public static void showHowWon(int tankId) {
        ImageView image = new ImageView(new javafx.scene.image.Image("assets/fade_out.png"));
        root.getChildren().add(image);
        String text = tankId == 0 ? "YOU LOSE!" : "YOU WIN!";
        String color = tankId == 0 ? "#fb0000" : "#26e405";
        Label label = new Label(text);

        label.setTextFill(Color.web(color));
        label.setFont(new Font(60));
        label.setTranslateX(0);
        label.setTranslateY(0);

        root.getChildren().add(label);
    }
}
