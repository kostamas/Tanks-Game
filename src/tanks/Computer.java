package tanks;

import java.util.HashMap;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.jpl7.Atom;
import org.jpl7.Integer;
import org.jpl7.Query;
import org.jpl7.Term;

public class Computer {

    Tank computerTank;
    Tank humenTank;
    Timeline timeline;
    StackPane root;

    public Computer(Tank computerTank, Tank humenTank, StackPane root) {
        this.computerTank = computerTank;
        this.humenTank = humenTank;
        String consult = "consult('tanks.pl')";
        Query query = new Query(consult);
        query.hasSolution();
        startPlay();
        this.root = root;
    }

    private void startPlay() {
        timeline = new Timeline(new KeyFrame(Duration.millis(150), keyFrameFn -> animate()));
        timeline.setCycleCount(1000);
        timeline.play();
    }

    private KeyFrame animate() {
        int x = humenTank.getCurrentPosition()[0];
        int y = humenTank.getCurrentPosition()[1];
        int[] nextMove = new int[2];
        int nextX, nextY;

        nextMove = askForNextMove(x, y);
        nextX = nextMove[0];
        nextY = nextMove[1];

        moveTank(nextX, nextY);
        return null;
    }

    private void moveTank(int nextX, int nextY) {
        int currentDirection = computerTank.currentDirection;
        int currentX = computerTank.getCurrentPosition()[0];
        int currentY = computerTank.getCurrentPosition()[1];
        int nextMoveDirection = calcNextDirection(nextX, nextY, currentX, currentY);
        if (nextMoveDirection == -1) {
            return; // stay in place
        }

        if (currentDirection == nextMoveDirection) {
            computerTank.move(nextX, nextY, "assets/tank2_" + GameStatus.directions[nextMoveDirection] + ".png");
        } else {
            calcTurn(currentDirection, nextMoveDirection);
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
                this.computerTank.turnLeft();
            } else {
                this.computerTank.turnRight();
            }
        } else {
            if (turnOppositeDirection) {
                this.computerTank.turnRight();
            } else {
                this.computerTank.turnLeft();
            }
        }
    }

    private int[] askForNextMove(int x, int y) {
        String moves = buildMovesForProlog();
        String bestMove = "best_move(" + moves + "," + x + "-" + y + ",X-Y, SHOOT).";
        Query q3 = new Query(bestMove);
        java.util.HashMap[] solution;
        solution = (HashMap[]) q3.allSolutions();

        int[] nextMove = new int[2];
        String shoot;
        nextMove[0] = ((org.jpl7.Integer) solution[0].get("X")).intValue();
        nextMove[1] = ((org.jpl7.Integer) solution[0].get("Y")).intValue();
        shoot = ((Atom) solution[0].get("SHOOT")).name();
        if (shoot.equals("yes")) {

        this.computerTank.shot(this.root, this.computerTank);
        }
        return nextMove;
    }

    private String buildMovesForProlog() {
        String moves = "";
        int x = computerTank.getCurrentPosition()[0];
        int y = computerTank.getCurrentPosition()[1];

        moves += x + "-" + y;
        moves += isCollision(x, y + 10) ? "" : "," + (x) + "-" + (y + 10);
        moves += isCollision(x + 10, y + 10) ? "" : "," + (x + 10) + "-" + (y + 10);
        moves += isCollision(x + 10, y) ? "" : "," + (x + 10) + "-" + (y);
        moves += isCollision(x + 10, y - 10) ? "" : "," + (x + 10) + "-" + (y - 10);
        moves += isCollision(x, y - 10) ? "" : "," + x + "-" + (y - 10);
        moves += isCollision(x - 10, y - 10) ? "" : "," + (x - 10) + "-" + (y - 10);
        moves += isCollision(x - 10, y) ? "" : "," + (x - 10) + "-" + y;
        moves += isCollision(x - 10, y + 10) ? "" : "," + (x - 10) + "-" + (y + 10);

        moves = "[" + moves + "]";
        return moves;
    }

    private boolean isCollision(int x, int y) {
        int[][] walls = Walls.walls;
        int wallLength = Walls.length;

        for (int i = 0; i < walls.length; i++) {
            boolean XCollision = (x + this.computerTank.tankLentgh >= walls[i][0] && x < walls[i][0] + wallLength);
            boolean YCollision = (y + this.computerTank.tankLentgh >= walls[i][1] && y < walls[i][1] + wallLength);
            if (XCollision && YCollision) {
                return true;
            }
        }
        return false;
    }

}
