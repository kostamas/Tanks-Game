package tanks;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

public class Humen {

    private Tank activeTank;
    private Tank[] humenTanks;
    StackPane root;
    GameController gameController;


    public Humen(Tank activeTank, Tank[] humenTanks, StackPane root, GameController gameController) {
        this.activeTank = activeTank;
        this.humenTanks = humenTanks;
        this.root = root;
        this.gameController = gameController;
    }

    public void eventHanlder(KeyEvent event) {
        if (GameController.getActivePlayer() != TankConst.HUMEN) {
            return;
        }

        if (event.getCode() == KeyCode.DIGIT1) {
            activeTank = humenTanks[0];
        }

        if (event.getCode() == KeyCode.DIGIT2) {
            activeTank = humenTanks[1];
        }
        
         if (event.getCode() == KeyCode.DIGIT3) {
            activeTank = humenTanks[2];
        }

        if (event.getCode() == KeyCode.RIGHT) {
            activeTank.turnRight();
        }
        if (event.getCode() == KeyCode.LEFT) {
            activeTank.turnLeft();
        }

        if (event.getCode() == KeyCode.UP) {
            moveTank("tank1_", TankConst.tankMoveLength);
        }
        if (event.getCode() == KeyCode.DOWN) {
            moveTank("tank1_", -TankConst.tankMoveLength);
        }

        if (event.getCode() == KeyCode.ENTER) {
            activeTank.setStartedPostion(activeTank.getCurrentPosition()[0], activeTank.getCurrentPosition()[1]);
            gameController.turnHanlder(TankConst.COMPUTER);
        }

        if (event.getCode() == KeyCode.SPACE) {
            int direction = activeTank.getDirection();
            int x = activeTank.getCurrentPosition()[0] + TankConst.bulletOffsetByDirection[direction][0];
            int y = activeTank.getCurrentPosition()[1] + TankConst.bulletOffsetByDirection[direction][1];
            int nextX = 0, nextY = 0;

            if (direction != 0 && direction != 4) {
                nextX += (direction == 1 || direction == 2 || direction == 3) ? 10 : -10;
            }

            if (direction != 2 && direction != 6) {
                nextY += (direction == 0 || direction == 1 || direction == 7) ? -10 : 10;
            }
            activeTank.shot(root, activeTank);
        }
    }
    
    private void moveTank(String tankName, int tankMoveDiff) {
        int x = activeTank.getCurrentPosition()[0];
        int y = activeTank.getCurrentPosition()[1];
        int startedX = activeTank.getStartedPosition()[0];
        int startedY = activeTank.getStartedPosition()[1];
        int maxDistance = TankConst.STEPS * TankConst.tankMoveLength;
        
        int direction = activeTank.getDirection();
        if (direction != 0 && direction != 4) {
            x += (direction == 1 || direction == 2 || direction == 3) ? tankMoveDiff : -tankMoveDiff;
        }
        if (direction != 2 && direction != 6) {
            y += (direction == 0 || direction == 1 || direction == 7) ? -tankMoveDiff : tankMoveDiff;
        }

        if (Math.abs(x - startedX) <= maxDistance && Math.abs(y - startedY) <= maxDistance) {
            activeTank.move(x, y, "assets/" + tankName + GameStatus.directions[direction] + ".png");
        }
    }

}
