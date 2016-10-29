package tanks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.jpl7.Atom;
import org.jpl7.Compound;
import org.jpl7.Integer;
import org.jpl7.Query;
import org.jpl7.Term;

public class Computer {

    Tank[] computerTanks;
    Tank[] humenTanks;
    Tank activeTank;
    Timeline timeline;
    StackPane root;

    public Computer(Tank[] computerTanks, Tank[] humenTanks, StackPane root) {
        this.computerTanks = computerTanks;
        this.humenTanks = humenTanks;
        String consult = "consult('alpha-beta.pl')";
        Query query = new Query(consult);
        query.hasSolution();

        this.root = root;
    }

    public void play() {
        int[] nextMove = new int[2];
        int nextX, nextY;

        nextMove = askForNextMove();
        nextX = nextMove[0];
        nextY = nextMove[1];

        timeline = new Timeline(new KeyFrame(Duration.millis(450), keyFrameFn -> animate(nextX, nextY)));
        timeline.setCycleCount(1);
        timeline.play();
    }

    private KeyFrame animate(int nextX, int nextY) {
        moveTank(nextX, nextY);
        Timeline timelineTurn = timeline;
        timelineTurn = new Timeline(new KeyFrame(Duration.millis(250), keyFrameFn -> animateTurn()));
        timelineTurn.setCycleCount(1);
        timelineTurn.play();

        return null;
    }

    private KeyFrame animateTurn() {
        setCorrectDirection();
        shootingHandler();
        GameController.setActivePlayer(TankConst.HUMEN);
        return null;
    }

    private void moveTank(int nextX, int nextY) {
        int currentDirection = activeTank.currentDirection;
        int currentX = activeTank.getCurrentPosition()[0];
        int currentY = activeTank.getCurrentPosition()[1];
        int nextMoveDirection = calcNextDirection(nextX, nextY, currentX, currentY);
        if (nextMoveDirection == -1) {
            return; // stay in place
        }

        if (currentDirection == nextMoveDirection) {
            activeTank.move(nextX, nextY, "assets/tank2_" + GameStatus.directions[nextMoveDirection] + ".png");
        } else {
            calcTurn(currentDirection, nextMoveDirection);
            moveTank(nextX, nextY);
        }
    }

    private int calcNextDirection(int nextX, int nextY, int currentX, int currentY) {
        String xDirection = "";
        String yDirection = "";
        String nextDirection;
        if (nextX != currentX) {
            xDirection = nextX - currentX > 0 ? "right" : "left";
        }
        if (nextY != currentY) {
            yDirection = nextY - currentY < 0 ? "up" : "down";
        }
        if (xDirection.length() > 1 && yDirection.length() > 1) {
            nextDirection = yDirection + "_" + xDirection;
        } else {
            nextDirection = xDirection.length() > 1 ? xDirection : yDirection;
        }

        for (int i = 0; i < GameStatus.directions.length; i++) {
            if (GameStatus.directions[i].equals(nextDirection)) {
                return i;
            }
        }
        return -1;
    }

    private void calcTurn(int currentDirection, int nextMoveDirection) {
        boolean turnRight = nextMoveDirection - currentDirection > 0;
        boolean turnOppositeDirection = Math.abs(nextMoveDirection - currentDirection) > 3;

        if (turnRight) {
            if (turnOppositeDirection) {
                this.activeTank.turnLeft();
            } else {
                this.activeTank.turnRight();
            }
        } else {
            if (turnOppositeDirection) {
                this.activeTank.turnRight();
            } else {
                this.activeTank.turnLeft();
            }
        }
    }

    private int[] askForNextMove() {
        String computerTanksPos = "";
        String humenTanksPos = "";

        for (int i = 0; i < this.computerTanks.length; i++) {
            int computerX = computerTanks[i].getCurrentPosition()[0];
            int computerY = computerTanks[i].getCurrentPosition()[1];
            int computerLife = computerTanks[i].getLife();
            if (computerLife > 0) {
                computerTanksPos += "[" + computerX + "," + computerY + "," + computerLife + "," + (i + 1) + "]";
                computerTanksPos += (i < this.computerTanks.length - 1) ? "," : "";
            }
        }

        for (int i = 0; i < this.humenTanks.length; i++) {
            int humenX = humenTanks[i].getCurrentPosition()[0];
            int humenY = humenTanks[i].getCurrentPosition()[1];
            int humenLife = humenTanks[i].getLife();
            if (humenLife > 0) {
                humenTanksPos += "[" + humenX + "," + humenY + "," + humenLife + "," + (i + 1) + "]";
                humenTanksPos += (i < this.humenTanks.length - 1) ? "," : "";
            }
        }

        String alphabetaPos = "[[" + computerTanksPos + "],[" + humenTanksPos + "], computer, 1]";

        String bestMoveQuery = "[CTanks,_,_,_]";
        String alphabetaQuery = "alphabeta(" + alphabetaPos + ",-999999999, 999999999," + bestMoveQuery + ", Val).";
        Query bestMove = new Query(alphabetaQuery);

        Map<String, Term> solution = bestMove.oneSolution();

        int[] nextMove = new int[2];
        int bestMoveTankNum;
        String shoot;
        Term[] terms = solution.get("CTanks").toTermArray();

        for (int i = 0; i < terms.length; i++) {
            nextMove[0] = terms[i].toTermArray()[0].intValue();
            nextMove[1] = terms[i].toTermArray()[1].intValue();
            int tankNum = terms[i].toTermArray()[3].intValue();
            int currentTankX = this.computerTanks[tankNum - 1].getCurrentPosition()[0];
            int currentTanky = this.computerTanks[tankNum - 1].getCurrentPosition()[1];

            if (nextMove[0] != currentTankX || nextMove[1] != currentTanky) {

                this.activeTank = this.computerTanks[tankNum - 1];
                break;
            }
        }

        return nextMove;
    }

    private boolean isCollision(int x, int y) {
        int[][] walls = Walls.walls;
        int wallLength = Walls.length;

        for (int i = 0; i < walls.length; i++) {
            boolean XCollision = (x + this.activeTank.tankLentgh >= walls[i][0] && x < walls[i][0] + wallLength);
            boolean YCollision = (y + this.activeTank.tankLentgh >= walls[i][1] && y < walls[i][1] + wallLength);
            if (XCollision && YCollision) {
                return true;
            }
        }
        return false;
    }

    private void setCorrectDirection() {
        int cx = this.activeTank.getCurrentPosition()[0];
        int cy = this.activeTank.getCurrentPosition()[1];

        int hx = 0, hy = 0;
        int minDistance = 100000;
        for (int i = 0; i < this.humenTanks.length; i++) {
            int tmpX = this.humenTanks[i].getCurrentPosition()[0];
            int tmpY = this.humenTanks[i].getCurrentPosition()[1];
            if(Math.abs(tmpX-cx) + Math.abs(tmpY-cy) < minDistance){
                minDistance = Math.abs(tmpX-cx) + Math.abs(tmpY-cy);
                hx = tmpX;
                hy = tmpY;
            }
        }

        int nextDirection = -1;
        String yDirection = "";
        String xDirection = "";
        String direction = "";

        if (Math.abs(hy - cy) > 30) {
            yDirection = hy - cy < 0 ? "up" : "down";
        }

        if (Math.abs(hx - cx) > 30) {
            xDirection = hx - cx > 0 ? "right" : "left";
        }

        if (yDirection.length() > 0 && xDirection.length() > 0) {
            direction = yDirection + "_" + xDirection;
        } else {
            direction = yDirection + xDirection;
        }

        for (int i = 0; i < TankConst.directions.length; i++) {
            if (TankConst.directions[i].equals(direction)) {
                nextDirection = i;
            }
        }

        if (this.activeTank.getDirection() != nextDirection) {
            calcTurn(this.activeTank.getDirection(), nextDirection);
            setCorrectDirection();
        }

    }

    private void shootingHandler() {
        int cx = this.activeTank.getCurrentPosition()[0];
        int cy = this.activeTank.getCurrentPosition()[1];
        setCorrectDirection();
        for (int i = 0; i < this.humenTanks.length; i++) {
            int hx = this.humenTanks[i].getCurrentPosition()[0];
            int hy = this.humenTanks[i].getCurrentPosition()[1];

            if (Math.abs(cx - hx) <= 50 && Math.abs(cy - hy) <= 50) {
                this.activeTank.shot(root, this.activeTank);
                break;
            }
        }

    }

}
