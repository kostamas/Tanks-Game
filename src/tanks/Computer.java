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
    int alphabetaDepth;

    public Computer(Tank[] computerTanks, Tank[] humenTanks, StackPane root) {
        this.computerTanks = computerTanks;
        this.humenTanks = humenTanks;
        String consult = "consult('alpha-beta.pl')";
        Query query = new Query(consult);
        query.hasSolution();

        this.root = root;
    }

    public void setAlphabetaDepth(int depth) {
        this.alphabetaDepth = depth;
    }

    public void play() {
        int[] nextMove = new int[2];
        int nextX, nextY, shootX, shootY;
        int[] queryResult;
        queryResult = askForNextMove();
        nextX = queryResult[0];
        nextY = queryResult[1];
        shootX = queryResult[2];
        shootY = queryResult[3];

        if (nextX > 0 && nextY > 0) {
            timeline = new Timeline(new KeyFrame(Duration.millis(450), keyFrameFn -> animate(nextX, nextY, shootX, shootY)));
            timeline.setCycleCount(1);
            timeline.play();
        } else {
            generalShootingHandler(shootX, shootY);
            GameController.setActivePlayer(TankConst.HUMEN);
        }
    }

    private KeyFrame animate(int nextX, int nextY, int shootX, int shootY) {
        moveTank(nextX, nextY);
        Timeline timelineTurn = timeline;
        timelineTurn = new Timeline(new KeyFrame(Duration.millis(250), keyFrameFn -> animateTurn(shootX, shootY)));
        timelineTurn.setCycleCount(1);
        timelineTurn.play();

        return null;
    }

    private KeyFrame animateTurn(int shootX, int shootY) {
        setCorrectDirection(this.activeTank, -1, -1);
        shootingHandler(shootX, shootY);
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
            activeTank.move(nextX, nextY, "assets/tank2_" + GameStatus.directions[nextMoveDirection] + activeTank.getTankNumber() + ".png");
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
            int computerPower = computerTanks[i].getPower();

            if (computerLife > 0) {
                computerTanksPos += firstTank ? "" : ",";
                computerTanksPos += "[" + computerX + "," + computerY + "," + computerLife + "," + (i + 1) + "," + computerPower + "]";
                firstTank = false;
            }
        }

        firstTank = true;
        for (int i = 0; i < this.humenTanks.length; i++) {
            int humenX = humenTanks[i].getCurrentPosition()[0];
            int humenY = humenTanks[i].getCurrentPosition()[1];
            int humenLife = humenTanks[i].getLife();
            int humenPower = humenTanks[i].getPower();

            if (humenLife > 0) {
                humenTanksPos += firstTank ? "" : ",";
                humenTanksPos += "[" + humenX + "," + humenY + "," + humenLife + "," + (i + 1) + "," + humenPower + "]";
                firstTank = false;
            }
        }

        String alphabetaPos = "[[" + computerTanksPos + "],[" + humenTanksPos + "], computer," + this.alphabetaDepth + ",_,_]";

        String bestMoveQuery = "[CTanks,_,_,_,[NextCX,NextCY,_,NextCNum,_],[HXToShoot,HYToShoot,_,_,_]]";
        String alphabetaQuery = "alphabeta(" + alphabetaPos + ",-999999, 999999," + bestMoveQuery + ", Val).";
        Query bestMove = new Query(alphabetaQuery);

        Map<String, Term> solution = bestMove.oneSolution();

        int[] result = new int[4];
        int tankNum = 0;
        result[0] = result[1] = -1;
        int bestMoveTankNum, shootX, shootY;
        String shoot;
        Term[] terms = solution.get("CTanks").toTermArray();

        tankNum = solution.get("NextCNum").intValue();
        result[0] = solution.get("NextCX").intValue();
        result[1] = solution.get("NextCY").intValue();
        result[2] = solution.get("HXToShoot").intValue();
        result[3] = solution.get("HYToShoot").intValue();

        this.activeTank = this.computerTanks[tankNum - 1];

        return result;
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

    private void setCorrectDirection(Tank cTank, int shootX, int shootY) {
        int cx = cTank.getCurrentPosition()[0];
        int cy = cTank.getCurrentPosition()[1];

        int hx = 0, hy = 0;
        int minDistance = 100000;
        if (shootX == -1 || shootY == -1) {
            for (int i = 0; i < this.humenTanks.length; i++) {
                int tmpX = this.humenTanks[i].getCurrentPosition()[0];
                int tmpY = this.humenTanks[i].getCurrentPosition()[1];
                if (Math.abs(tmpX - cx) + Math.abs(tmpY - cy) < minDistance && this.humenTanks[i].getLife() > 0) {
                    minDistance = Math.abs(tmpX - cx) + Math.abs(tmpY - cy);
                    hx = tmpX;
                    hy = tmpY;
                }
            }
        } else {
            hx = shootX;
            hy = shootY;
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
            setCorrectDirection(cTank, hx, hy);
        }

    }

    private void shootingHandler(int shootX, int shootY) {
        int cx = this.activeTank.getCurrentPosition()[0];
        int cy = this.activeTank.getCurrentPosition()[1];
        setCorrectDirection(this.activeTank, shootX, shootY);
        for (int i = 0; i < this.humenTanks.length; i++) {
            int hx = this.humenTanks[i].getCurrentPosition()[0];
            int hy = this.humenTanks[i].getCurrentPosition()[1];

            if (Math.abs(cx - hx) <= 50 && Math.abs(cy - hy) <= 50 && this.humenTanks[i].getLife() > 0 && activeTank.getLife() > 0) {
                this.activeTank.shot(root, this.activeTank, activeTank.getPower());
                break;
            }
        }
    }

    private void generalShootingHandler(int shootX, int shootY) {
        boolean isShooted = false;
        for (int i = 0; !isShooted && i < this.computerTanks.length; i++) {
            int cx = this.computerTanks[i].getCurrentPosition()[0];
            int cy = this.computerTanks[i].getCurrentPosition()[1];

            for (int j = 0; !isShooted && j < this.humenTanks.length; j++) {
                int hx = this.humenTanks[j].getCurrentPosition()[0];
                int hy = this.humenTanks[j].getCurrentPosition()[1];

                if (Math.abs(cx - hx) <= 50 && Math.abs(cy - hy) <= 50 && this.humenTanks[j].getLife() > 0 && this.computerTanks[i].getLife() > 0) {
                    setCorrectDirection(this.computerTanks[i], shootX, shootY);
                    this.computerTanks[i].shot(root, this.computerTanks[i], this.computerTanks[i].power);
                    isShooted = true;
                }
            }
        }
    }
}
