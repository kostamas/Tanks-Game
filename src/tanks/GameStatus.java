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
        humenStatus.setFont(new Font(14));
        humenStatus.setTranslateX(-215);
        humenStatus.setTranslateY(-150);

        computerStatus.setTextFill(Color.web("#ffa114"));
        computerStatus.setFont(new Font(14));
        computerStatus.setTranslateX(180);
        computerStatus.setTranslateY(-150);

        root.getChildren().add(computerStatus);
        root.getChildren().add(humenStatus);
    }

    public static boolean checkIfTankeHit(int bulletXPosition, int bulletYPosition, int bulletLength, Tank shootingTank, int power) {

        for (int i = 0; i < GameStatus.computerTanks.length; i++) {
            int tankXPosition = GameStatus.computerTanks[i].getCurrentPosition()[0];
            int tankYPosition = GameStatus.computerTanks[i].getCurrentPosition()[1];
            boolean XCollision = (bulletXPosition >= tankXPosition && bulletXPosition < tankXPosition + TankConst.tankLength);
            boolean YCollision = (bulletYPosition >= tankYPosition && bulletYPosition < tankYPosition + TankConst.tankLength);
            if (XCollision && YCollision && GameStatus.computerTanks[i] != shootingTank && GameStatus.computerTanks[i].getLife() > 0) {
                handleTankHit(GameStatus.computerTanks[i], power);
                return true;
            }
        }

        for (int i = 0; i < GameStatus.humenTanks.length; i++) {
            int tankXPosition = GameStatus.humenTanks[i].getCurrentPosition()[0];
            int tankYPosition = GameStatus.humenTanks[i].getCurrentPosition()[1];
            boolean XCollision = (bulletXPosition >= tankXPosition && bulletXPosition < tankXPosition + TankConst.tankLength);
            boolean YCollision = (bulletYPosition >= tankYPosition && bulletYPosition < tankYPosition + TankConst.tankLength);
            if (XCollision && YCollision && GameStatus.humenTanks[i] != shootingTank && GameStatus.humenTanks[i].getLife() > 0) {
                handleTankHit(GameStatus.humenTanks[i], power);
                return true;
            }
        }

        return false;
    }

    public static void handleTankHit(Tank tank, int power) {
        tank.hitted(power);
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

        if (isHumenLost || isComputerLost && !gameFinished) {
            int tankId = isHumenLost ? TankConst.COMPUTER : TankConst.HUMEN;
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

    static void checkIfSomeoneWon() {
        boolean someoneWon = true;
        int cSumLife = 0, hSumeLife = 0;
        for (int i = 0; i < GameStatus.computerTanks.length; i++) {
            if (GameStatus.computerTanks[i].getLife() > 0) {
                for (int j = 0; j < GameStatus.humenTanks.length; j++) {
                    int cx = GameStatus.computerTanks[i].getCurrentPosition()[0];
                    int hx = GameStatus.humenTanks[j].getCurrentPosition()[0];
                    if (GameStatus.humenTanks[j].getLife() > 0 && cx + 50 >= hx) {
                        someoneWon = false;
                    }
                }
            }
        }

        if (someoneWon) {
            GameStatus.gameFinished = true;
            for (int i = 0; i < GameStatus.computerTanks.length; i++) {
                cSumLife += GameStatus.computerTanks[i].getLife();
            }

            for (int j = 0; j < GameStatus.humenTanks.length; j++) {
                hSumeLife += GameStatus.humenTanks[j].getLife();
            }

            if (cSumLife > hSumeLife) {
                showHowWon(TankConst.COMPUTER);
            } else {
                showHowWon(TankConst.HUMEN);

            }
        }

    }
}
