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

    Tank computerTank;
    Tank humenTank;
    Timeline timeline;
    StackPane root;

    public Computer(Tank computerTank, Tank humenTank, StackPane root) {
        this.computerTank = computerTank;
        this.humenTank = humenTank;
        String consult = "consult('alpha-beta.pl')";
        Query query = new Query(consult);
        query.hasSolution();

        this.root = root;
    }

    public void play() {
        int x = humenTank.getCurrentPosition()[0];
        int y = humenTank.getCurrentPosition()[1];
        int[] nextMove = new int[2];
        int nextX, nextY;

        nextMove = askForNextMove(x, y);
        nextX = nextMove[0];
        nextY = nextMove[1];

        findPathAndMoveTank(nextX, nextY);
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

    private int[] askForNextMove(int humenX, int humenY) {

        int computerX = computerTank.getCurrentPosition()[0];
        int computerY = computerTank.getCurrentPosition()[1];

        String alphabetaPos = "[" + computerX + "-" + computerY + "," + humenX + "-" + humenY + ", computer, 1]";
        String bestMoveQuery = "[ComputerX-ComputerY,_,_,_]";
        String alphabetaQuery = "alphabeta(" + alphabetaPos + ",-100000, 100000," + bestMoveQuery + ", Val).";
        Query bestMove = new Query(alphabetaQuery);

        Map<String, Term> solution = bestMove.oneSolution();

        int[] nextMove = new int[2];
        String shoot;
        nextMove[0] = ((org.jpl7.Integer) solution.get("ComputerX")).intValue();
        nextMove[1] = ((org.jpl7.Integer) solution.get("ComputerY")).intValue();
        return nextMove;
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

    private void findPathAndMoveTank(int nextX, int nextY) {
        int[] target = new int[2];
        target[0] = nextX;
        target[1] = nextY;
        List<int[]> path = new ArrayList<int[]>();
        int currentX = computerTank.getCurrentPosition()[0];
        int currentY = computerTank.getCurrentPosition()[1];
        List<int[]> result = findPath(target, currentX, currentY, path, 1);
        timeline = new Timeline(new KeyFrame(Duration.millis(250), keyFrameFn -> animate(result)));
        timeline.setCycleCount(result.size() + 1);  // size + 1: beacause need to run shootingHandler.
        timeline.play();
    }

    private KeyFrame animate(List<int[]> path) {
        if (path.size() == 0) {
            setCorrectDirection();
            shootingHandler();
            timeline.stop();
            GameController.setActivePlayer(TankConst.HUMEN);
            return null;
        }
        int[] nextMove = path.remove(0);
        int nextX, nextY;
        nextX = nextMove[0];
        nextY = nextMove[1];

        moveTank(nextX, nextY);
        return null;
    }

    private List<int[]> findPath(int[] target, int x, int y, List<int[]> path, int depth) {
        if (depth >= 5) {
            return null;
        }
        if (x == target[0] && y == target[1]) {
            return path;
        }
        int[][] neighbors = getAllNeighbors(x, y);
        for (int i = 0; i < neighbors.length; i++) {
            int neighborX = neighbors[i][0], neighborY = neighbors[i][1];
            if (!isPathContain(neighborX, neighborY, path) && !Walls.collision(neighborX, neighborY, TankConst.tankLength)) {
                path.add(neighbors[i]);
                List<int[]> tempPath = findPath(target, neighbors[i][0], neighbors[i][1], path, (depth + 1));
                if (tempPath != null) {
                    return tempPath;
                }
                path.remove(neighbors[i]);
            }

        }
        return null;
    }

    private int[][] getAllNeighbors(int x, int y) {
        int[][] neighbors = new int[8][2];
        neighbors[0] = new int[]{x, y + TankConst.tankMoveLength};
        neighbors[1] = new int[]{x + TankConst.tankMoveLength, y + TankConst.tankMoveLength};
        neighbors[2] = new int[]{x + TankConst.tankMoveLength, y};
        neighbors[3] = new int[]{x + TankConst.tankMoveLength, y - TankConst.tankMoveLength};
        neighbors[4] = new int[]{x, y - TankConst.tankMoveLength};
        neighbors[5] = new int[]{x - TankConst.tankMoveLength, y - TankConst.tankMoveLength};
        neighbors[6] = new int[]{x - TankConst.tankMoveLength, y};
        neighbors[7] = new int[]{x - TankConst.tankMoveLength, y + TankConst.tankMoveLength};
        return neighbors;
    }

    private boolean isPathContain(int x, int y, List<int[]> path) {
        for (int[] temp : path) {
            if (temp[0] == x && temp[1] == y) {
                return true;
            }
        }
        return false;
    }

    private void setCorrectDirection() {
        int cx = this.computerTank.getCurrentPosition()[0];
        int cy = this.computerTank.getCurrentPosition()[1];
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

        if (this.computerTank.getDirection() != nextDirection) {
            calcTurn(this.computerTank.getDirection(), nextDirection);
            setCorrectDirection();
        }

    }

    private void shootingHandler() {
        int cx = this.computerTank.getCurrentPosition()[0];
        int cy = this.computerTank.getCurrentPosition()[1];
        int hx = this.humenTank.getCurrentPosition()[0];
        int hy = this.humenTank.getCurrentPosition()[1];

        int distance = Math.abs(cx - hx) + Math.abs(cy - hy);

        if (distance > TankConst.shootingArea) {
            return;
        }

        if ( Math.abs(cx - hx) < 20 || Math.abs(cy - hy) < 20 || Math.abs(Math.abs(cx - hx) - Math.abs(cy - hy)) < 20) {
            this.computerTank.shot(root, this.computerTank);
        }
    }

}
