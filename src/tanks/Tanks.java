package tanks;

import org.jpl7.*;
import java.util.HashMap;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class Tanks extends Application implements EventHandler<KeyEvent> {

    private TilePane tilePane;
    private Scene scene;
    private Tank myTank;
    private Tank computerTank;
    int tankMoveDiff = 10;
    StackPane root;
    int[][] bulletOffsetByDirection = {{10, 0}, {30, 0}, {37, 11}, {35, 35}, {10, 35}, {-5, 35}, {0, 11}, {0, 0}};

    public static void main(String[] args) {

        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        root = new StackPane();

        BackgroundImage myBI = new BackgroundImage(new Image("assets/background.png", 1300, 630, false, true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        root.setBackground(new Background(myBI));

        myTank = new Tank(50, 50, 3, 30, "assets/tank1_down_right.png", GameStatus.directions, "assets/tank1_", "Humen");
        computerTank = new Tank(1200, 500, 7, 30, "assets/tank2_up_left.png", GameStatus.directions, "assets/tank2_", "Computer");

        root.getChildren().add(myTank);
        root.getChildren().add(computerTank);

        Walls walls = new Walls(50, "assets/wall.png");
        root.getChildren().add(walls);
        Scene scene = new Scene(root, 1300, 600);
        scene.setOnKeyPressed(this);

        primaryStage.setScene(scene);
        new GameStatus(myTank, computerTank, root);

        primaryStage.show();
        Computer computer = new Computer(computerTank, myTank,root);

    }

    @Override
    public void handle(KeyEvent event) {
        this.tilePane = null;
        if (event.getCode() == KeyCode.RIGHT) {
            myTank.turnRight();
        }
        if (event.getCode() == KeyCode.LEFT) {
            myTank.turnLeft();
        }

        if (event.getCode() == KeyCode.UP) {
            moveTank("tank1_", tankMoveDiff);
        }
        if (event.getCode() == KeyCode.DOWN) {
            moveTank("tank1_", -tankMoveDiff);
        }

        if (event.getCode() == KeyCode.SPACE) {
            int direction = myTank.getDirection();
            int x = myTank.getCurrentPosition()[0] + bulletOffsetByDirection[direction][0];
            int y = myTank.getCurrentPosition()[1] + bulletOffsetByDirection[direction][1];
            int nextX = 0, nextY = 0;

            if (direction != 0 && direction != 4) {
                nextX += (direction == 1 || direction == 2 || direction == 3) ? 10 : -10;
            }

            if (direction != 2 && direction != 6) {
                nextY += (direction == 0 || direction == 1 || direction == 7) ? -10 : 10;
            }
            myTank.shot(root, myTank);
        }
    }

    private void moveTank(String tankName, int tankMoveDiff) {
        int x = myTank.getCurrentPosition()[0];
        int y = myTank.getCurrentPosition()[1];
        int direction = myTank.getDirection();
        if (direction != 0 && direction != 4) {
            x += (direction == 1 || direction == 2 || direction == 3) ? tankMoveDiff : -tankMoveDiff;
        }
        if (direction != 2 && direction != 6) {
            y += (direction == 0 || direction == 1 || direction == 7) ? -tankMoveDiff : tankMoveDiff;
        }
        myTank.move(x, y, "assets/" + tankName + GameStatus.directions[direction] + ".png");
    }
}
