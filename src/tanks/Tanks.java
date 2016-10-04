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
    private Tank tank1;
    private Tank tank2;
    int tankMoveDiff = 10;
    StackPane root;
    String[] directions = {"up", "up_right", "right", "down_right", "down", "down_left", "left", "up_left"};
    int [][]bulletOffsetByDirection = {{10,0},{30,0},{37,11},{35,35},{10,35},{-5,35},{0,11},{0,0}};

    public static void main(String[] args) {
        String s1 = "consult('test1.pl')";
        Query q1 = new Query(s1);
        System.out.println(q1.hasSolution());
        String s2 = "a(X)";
        Query q2 = new Query(s2);
        java.util.HashMap[] solution;

        solution = (HashMap[]) q2.allSolutions();
        for (java.util.HashMap sol : solution) {
            System.out.println(sol);
        }
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        root = new StackPane();

        BackgroundImage myBI = new BackgroundImage(new Image("assets/background.png", 1300, 630, false, true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        root.setBackground(new Background(myBI));

        tank1 = new Tank(50, 50, 3, 30, "assets/tank1_down_right.png", this.directions, "assets/tank1_");
        tank2 = new Tank(1200, 500, 0, 30, "assets/tank2_up_left.png", this.directions, "tank2");

        root.getChildren().add(tank1);
        root.getChildren().add(tank2);

        Walls walls = new Walls(50, "assets/wall.png");
        root.getChildren().add(walls);
        Scene scene = new Scene(root, 1300, 600);
        scene.setOnKeyPressed(this);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void handle(KeyEvent event) {
        this.tilePane = null;
        if (event.getCode() == KeyCode.RIGHT) {
            tank1.turnRight();
        }
        if (event.getCode() == KeyCode.LEFT) {
            tank1.turnLeft();
        }

        if (event.getCode() == KeyCode.UP) {
            moveTank("tank1_", tankMoveDiff);
        }
        if (event.getCode() == KeyCode.DOWN) {
            moveTank("tank1_", -tankMoveDiff);
        }

        if (event.getCode() == KeyCode.SPACE) {
            int direction = tank1.getDirection();
            int x = tank1.getCurrentPosition()[0] + bulletOffsetByDirection[direction][0];
            int y = tank1.getCurrentPosition()[1] + bulletOffsetByDirection[direction][1];
            int nextX = 0, nextY = 0;

            if (direction != 0 && direction != 4) {
                nextX += (direction == 1 || direction == 2 || direction == 3) ? 10 : -10;
            }

            if (direction != 2 && direction != 6) {
                nextY += (direction == 0 || direction == 1 || direction == 7) ? -10 : 10;
            }
            tank1.shot(x, y, nextX, nextY, root);
        }
    }

    private void moveTank(String tankName, int tankMoveDiff) {
        int x = tank1.getCurrentPosition()[0];
        int y = tank1.getCurrentPosition()[1];
        int direction = tank1.getDirection();
        if (direction != 0 && direction != 4) {
            x += (direction == 1 || direction == 2 || direction == 3) ? tankMoveDiff : -tankMoveDiff;
        }
        if (direction != 2 && direction != 6) {
            y += (direction == 0 || direction == 1 || direction == 7) ? -tankMoveDiff : tankMoveDiff;
        }
        tank1.move(x, y, "assets/" + tankName + directions[direction] + ".png");
    }
}
