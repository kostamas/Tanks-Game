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

    Tank []computerTanks;
    Tank activeTank;
    Tank humenTank;
    Timeline timeline;
    StackPane root;
    

    public Computer(Tank []computerTanks, Tank humenTank, StackPane root) {
        this.computerTanks = computerTanks;
        this.humenTank = humenTank;
        String consult = "consult('alpha-beta.pl')";
        Query query = new Query(consult);
        query.hasSolution();

        this.root = root;
    }

    public void play() {
        int x = humenTank.getCurrentPosition()[0];
        int y = humenTank.getCurrentPosition()[1];
        int life = humenTank.getLife();

        int[] nextMove = new int[2];
        int nextX, nextY;

        nextMove = askForNextMove(x, y, life);
        nextX = nextMove[0];
        nextY = nextMove[1];

        timeline = new Timeline(new KeyFrame(Duration.millis(1450), keyFrameFn -> animate(nextX, nextY)));
        timeline.setCycleCount(1);  // size + 1: beacause need to run shootingHandler.
        timeline.play();
    }

    private KeyFrame animate(int nextX, int nextY) {
        moveTank(nextX, nextY);
        Timeline timelineTurn = timeline;
        timelineTurn = new Timeline(new KeyFrame(Duration.millis(250), keyFrameFn -> animateTurn()));
        timelineTurn.setCycleCount(1);  // size + 1: beacause need to run shootingHandler.
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

    private int[] askForNextMove(int humenX, int humenY, int humenLife) {

        int computer1X = computerTanks[0].getCurrentPosition()[0];
        int computer1Y = computerTanks[0].getCurrentPosition()[1];
        int computer1Life = computerTanks[0].getLife();
        
        int computer2X = computerTanks[1].getCurrentPosition()[0];
        int computer2Y = computerTanks[1].getCurrentPosition()[1];
        int computer2Life = computerTanks[1].getLife();

        String computer1Pos = "[" + computer1X + "," + computer1Y + "," + computer1Life + ",1]";
        String computer2Pos = "[" + computer2X + "," + computer2Y + "," + computer2Life + ",2]";
        String computerPos = computer1Pos + "," + computer2Pos;
        
        String humenPos = "[[" + humenX + "," + humenY + "," + humenLife + ",1]]";

        String alphabetaPos = "[[" + computerPos + "]," + humenPos + ", computer, 1]";

        String bestMoveQuery = "[[ComputerX,ComputerY,Life,Num],_,_,_]";
        String alphabetaQuery = "alphabeta(" + alphabetaPos + ",-900000, 900000," + bestMoveQuery + ", Val).";
        Query bestMove = new Query(alphabetaQuery);

        Map<String, Term> solution = bestMove.oneSolution();

        int[] nextMove = new int[2];
        int bestMoveTankNum;
        String shoot;
        nextMove[0] = ((org.jpl7.Integer) solution.get("ComputerX")).intValue();
        nextMove[1] = ((org.jpl7.Integer) solution.get("ComputerY")).intValue();
        bestMoveTankNum = ((org.jpl7.Integer) solution.get("Num")).intValue();
        activeTank = computerTanks[bestMoveTankNum - 1];
        
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
        int hx = this.humenTank.getCurrentPosition()[0];
        int hy = this.humenTank.getCurrentPosition()[1];
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
        int hx = this.humenTank.getCurrentPosition()[0];
        int hy = this.humenTank.getCurrentPosition()[1];

        if (Math.abs(cx - hx) > TankConst.shootingArea || Math.abs(cy - hy) > TankConst.shootingArea) {
            return;
        }

        if (Math.abs(cx - hx) < 20 || Math.abs(cy - hy) < 20 || Math.abs(Math.abs(cx - hx) - Math.abs(cy - hy)) < 20) {
            this.activeTank.shot(root, this.activeTank);
        }
    }

}
