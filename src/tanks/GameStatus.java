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

    static Tank[] computerTanks;
    static Tank[] humenTanks;
    static Label computerStatus;
    static Label humenStatus;
    static StackPane root;
    static boolean gameFinished = false;
    static String[] directions = {"up", "up_right", "right", "down_right", "down", "down_left", "left", "up_left"};

    public GameStatus(Tank[] computerTanks, Tank[] humenTanks, StackPane root) {
        GameStatus.computerTanks = computerTanks;
        GameStatus.humenTanks = humenTanks;
        this.root = root;
        updateText();
    }

    public static void updateText() {
        root.getChildren().remove(computerStatus);
        root.getChildren().remove(humenStatus);

        String computerTanksText = "";
        String humneTanksText = "";

        for (int i = 0; i < GameStatus.computerTanks.length; i++) {
            computerTanksText += "   tank " + (i + 1) + ": " + GameStatus.computerTanks[i].getLife();
        }

        for (int i = 0; i < GameStatus.humenTanks.length; i++) {
            humneTanksText += "   tank " + (i + 1) + ": " + GameStatus.humenTanks[i].getLife();
        }

        humenStatus = new Label("YOUR TANKS LIFE:" + humneTanksText);
        computerStatus = new Label("COMPUTER TANKS LIFE:" + computerTanksText);

        humenStatus.setTextFill(Color.web("#48ff24"));
        humenStatus.setFont(new Font(15));
        humenStatus.setTranslateX(-220);
        humenStatus.setTranslateY(-225);

        computerStatus.setTextFill(Color.web("#ffa114"));
        computerStatus.setFont(new Font(15));
        computerStatus.setTranslateX(190);
        computerStatus.setTranslateY(-225);

        root.getChildren().add(computerStatus);
        root.getChildren().add(humenStatus);
    }

    public static boolean checkIfTankeHit(int bulletXPosition, int bulletYPosition, int bulletLength, Tank shootingTank) {
        for (int i = 0; i < GameStatus.computerTanks.length; i++) {
            int tankXPosition = GameStatus.computerTanks[i].getCurrentPosition()[0];
            int tankYPosition = GameStatus.computerTanks[i].getCurrentPosition()[1];
            boolean XCollision = (bulletXPosition >= tankXPosition && bulletXPosition < tankXPosition + TankConst.tankLength);
            boolean YCollision = (bulletYPosition >= tankYPosition && bulletYPosition < tankYPosition + TankConst.tankLength);
            if (XCollision && YCollision && GameStatus.computerTanks[i] != shootingTank) {
                handleTankHit(GameStatus.computerTanks[i]);
                return true;
            }
        }

        for (int i = 0; i < GameStatus.humenTanks.length; i++) {
            int tankXPosition = GameStatus.humenTanks[i].getCurrentPosition()[0];
            int tankYPosition = GameStatus.humenTanks[i].getCurrentPosition()[1];
            boolean XCollision = (bulletXPosition >= tankXPosition && bulletXPosition < tankXPosition + TankConst.tankLength);
            boolean YCollision = (bulletYPosition >= tankYPosition && bulletYPosition < tankYPosition + TankConst.tankLength);
            if (XCollision && YCollision && GameStatus.humenTanks[i] != shootingTank) {
                handleTankHit(GameStatus.humenTanks[i]);
                return true;
            }
        }

        return false;
    }

    public static void handleTankHit(Tank tank) {
        tank.hitted();
        updateText();

        boolean isComputerLost = true, isHumenLost = true;
        for (int i = 0; i < GameStatus.computerTanks.length; i++) {
            if (GameStatus.computerTanks[i].getLife() > 0) {
                isComputerLost = false;
            }

        }

        for (int i = 0; i < GameStatus.humenTanks.length; i++) {
            if (GameStatus.humenTanks[i].getLife() > 0) {
                isHumenLost = false;
            }

        }

        if (isHumenLost || isComputerLost && gameFinished) {
            int tankId = isHumenLost ? TankConst.COMPUTER : TankConst.COMPUTER;
            showHowWon(tankId);
            gameFinished = true;
        }
    }

    public static void showHowWon(int tankId) {
        ImageView image = new ImageView(new javafx.scene.image.Image("assets/fade_out.png"));
        root.getChildren().add(image);
        String text = TankConst.COMPUTER == tankId ? "YOU LOSE!" : "YOU WIN!";
        String color = TankConst.COMPUTER == tankId ? "#fb0000" : "#26e405";
        Label label = new Label(text);

        label.setTextFill(Color.web(color));
        label.setFont(new Font(60));
        label.setTranslateX(0);
        label.setTranslateY(0);

        root.getChildren().add(label);
    }
}
