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
    int tankMoveDiff = 25;
    String[] directions = {"up", "up_right", "right", "down_right", "down", "down_left", "left", "up_left"};

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
        StackPane root = new StackPane();

        BackgroundImage myBI = new BackgroundImage(new Image("assets/background.png", 1300, 630, false, true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        root.setBackground(new Background(myBI));

        Bullet bullet1 = new Bullet("assets/green_bullet.png", 16);
        Bullet bullet2 = new Bullet("assets/rey_bullet.png", 4);
        tank1 = new Tank(0, 0, 4, "assets/tank1_down.png", this.directions, "assets/tank1_", bullet1);
        tank2 = new Tank(1250, 550, 0, "assets/tank2_up.png", this.directions, "tank2", bullet2);

        root.getChildren().add(tank1);
        root.getChildren().add(tank2);

        root.getChildren().add(bullet1);
        root.getChildren().add(bullet2);

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
            int x = tank1.getCurrentPosition()[0];
            int y = tank1.getCurrentPosition()[1];
            int nextX = 0, nextY = 0;

            if (direction != 0 && direction != 4) {
                nextX += (direction == 1 || direction == 2 || direction == 3) ? 12 : -12;
            }

            if (direction != 2 && direction != 6) {
                nextY += (direction == 0 || direction == 1 || direction == 7) ? -12 : 12;
            }
            tank1.shot(x, y, nextX, nextY);
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
