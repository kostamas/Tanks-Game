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
        if (nextX > 0 && nextY > 0) {
            timeline = new Timeline(new KeyFrame(Duration.millis(450), keyFrameFn -> animate(nextX, nextY)));
            timeline.setCycleCount(1);
            timeline.play();
        } else {
            generalShootingHandler();
            GameController.setActivePlayer(TankConst.HUMEN);
        }
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
        setCorrectDirection(this.activeTank);
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
            calcTurn(currentDirection, nextMoveDirection, activeTank);
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

    private void calcTurn(int currentDirection, int nextMoveDirection, Tank cTank) {
        boolean turnRight = nextMoveDirection - currentDirection > 0;
        boolean turnOppositeDirection = Math.abs(nextMoveDirection - currentDirection) > 3;

        if (turnRight) {
            if (turnOppositeDirection) {
                cTank.turnLeft();
            } else {
                cTank.turnRight();
            }
        } else {
            if (turnOppositeDirection) {
                cTank.turnRight();
            } else {
                cTank.turnLeft();
            }
        }
    }

    private int[] askForNextMove() {
        String computerTanksPos = "";
        String humenTanksPos = "";

        boolean firstTank = true;
        for (int i = 0; i < this.computerTanks.length; i++) {
            int computerX = computerTanks[i].getCurrentPosition()[0];
            int computerY = computerTanks[i].getCurrentPosition()[1];
            int computerLife = computerTanks[i].getLife();
            if (computerLife > 0) {
                computerTanksPos += firstTank ? "" : ",";
                computerTanksPos += "[" + computerX + "," + computerY + "," + computerLife + "," + (i + 1) + "]";
                firstTank = false;
            }
        }

        firstTank = true;
        for (int i = 0; i < this.humenTanks.length; i++) {
            int humenX = humenTanks[i].getCurrentPosition()[0];
            int humenY = humenTanks[i].getCurrentPosition()[1];
            int humenLife = humenTanks[i].getLife();
            if (humenLife > 0) {
                humenTanksPos += firstTank ? "" : ",";
                humenTanksPos += "[" + humenX + "," + humenY + "," + humenLife + "," + (i + 1) + "]";
                firstTank = false;
            }
        }

        String alphabetaPos = "[[" + computerTanksPos + "],[" + humenTanksPos + "], computer, 1]";

        String bestMoveQuery = "[CTanks,_,_,_]";
        String alphabetaQuery = "alphabeta(" + alphabetaPos + ",-99999, 99999," + bestMoveQuery + ", Val).";
        Query bestMove = new Query(alphabetaQuery);

        Map<String, Term> solution = bestMove.oneSolution();

        int[] nextMove = new int[2];
        int bestMoveTankNum;
        String shoot;
        Term[] terms = solution.get("CTanks").toTermArray();

        for (int i = 0; i < terms.length; i++) {
            int tmpX = terms[i].toTermArray()[0].intValue();
            int tmpY = terms[i].toTermArray()[1].intValue();

            int tankNum = terms[i].toTermArray()[3].intValue();
            int currentTankX = this.computerTanks[tankNum - 1].getCurrentPosition()[0];
            int currentTanky = this.computerTanks[tankNum - 1].getCurrentPosition()[1];

            if (tmpX != currentTankX || tmpY != currentTanky) {

                nextMove[0] = tmpX;
                nextMove[1] = tmpY;
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
            boolean XCollision = (x + TankConst.tankLength >= walls[i][0] && x < walls[i][0] + wallLength);
            boolean YCollision = (y + TankConst.tankLength >= walls[i][1] && y < walls[i][1] + wallLength);
            if (XCollision && YCollision) {
                return true;
            }
        }
        return false;
    }

    private void setCorrectDirection(Tank cTank) {
        int cx = cTank.getCurrentPosition()[0];
        int cy = cTank.getCurrentPosition()[1];

        int hx = 0, hy = 0;
        int minDistance = 100000;
        for (int i = 0; i < this.humenTanks.length; i++) {
            int tmpX = this.humenTanks[i].getCurrentPosition()[0];
            int tmpY = this.humenTanks[i].getCurrentPosition()[1];
            if (Math.abs(tmpX - cx) + Math.abs(tmpY - cy) < minDistance && this.humenTanks[i].getLife() > 0) {
                minDistance = Math.abs(tmpX - cx) + Math.abs(tmpY - cy);
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

        if (cTank.getDirection() != nextDirection) {
            calcTurn(cTank.getDirection(), nextDirection, cTank);
            setCorrectDirection(cTank);
        }

    }

    private void shootingHandler() {
        int cx = this.activeTank.getCurrentPosition()[0];
        int cy = this.activeTank.getCurrentPosition()[1];
        setCorrectDirection(this.activeTank);
        for (int i = 0; i < this.humenTanks.length; i++) {
            int hx = this.humenTanks[i].getCurrentPosition()[0];
            int hy = this.humenTanks[i].getCurrentPosition()[1];

            if (Math.abs(cx - hx) <= 50 && Math.abs(cy - hy) <= 50 && this.humenTanks[i].getLife() > 0) {
                this.activeTank.shot(root, this.activeTank);
                break;
            }
        }
    }

    private void generalShootingHandler() {
        boolean isShooted = false;
        for (int i = 0; !isShooted && i < this.computerTanks.length; i++) {
            int cx = this.computerTanks[i].getCurrentPosition()[0];
            int cy = this.computerTanks[i].getCurrentPosition()[1];

            for (int j = 0; !isShooted && j < this.humenTanks.length; j++) {
                int hx = this.humenTanks[j].getCurrentPosition()[0];
                int hy = this.humenTanks[j].getCurrentPosition()[1];

                if (Math.abs(cx - hx) <= 50 && Math.abs(cy - hy) <= 50 && this.humenTanks[j].getLife() > 0) {
                    setCorrectDirection(this.computerTanks[i]);
                    this.computerTanks[i].shot(root, this.computerTanks[i]);
                    isShooted = true;
                }
            }

        }
    }

}
