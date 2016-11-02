package tanks;

import org.jpl7.*;
import java.util.HashMap;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

public class Tanks extends Application implements EventHandler<KeyEvent> {

    private TilePane tilePane;
    private Scene scene;
    GameController gameController;
    StackPane root;
    Humen humen;

    public static void main(String[] args) {

        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        root = new StackPane();

        BackgroundImage myBI = new BackgroundImage(new Image("assets/background.png", 1300, 630, false, true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        root.setBackground(new Background(myBI));

        Tank []humenTanks = new Tank[3];
        humenTanks[0] = new Tank(300, 100, 3, "assets/tank1_down_right.png", "assets/tank1_",root);
        humenTanks[1] = new Tank(350, 200, 3, "assets/tank1_down_right.png", "assets/tank1_",root);
        humenTanks[2] = new Tank(300, 300, 3, "assets/tank1_down_right.png", "assets/tank1_",root);
     
        Tank []computerTanks = new Tank[3];
        computerTanks[0] = new Tank(550,100, 7, "assets/tank2_up_left.png", "assets/tank2_",root);
        computerTanks[1] = new Tank(500,200, 7, "assets/tank2_up_left.png", "assets/tank2_",root);
        computerTanks[2] = new Tank(550,300, 7, "assets/tank2_up_left.png", "assets/tank2_",root);

        
        root.getChildren().add(computerTanks[0]);
        root.getChildren().add(computerTanks[1]);
        root.getChildren().add(computerTanks[2]);
        root.getChildren().add(humenTanks[0]);
        root.getChildren().add(humenTanks[1]);
        root.getChildren().add(humenTanks[2]);


        Walls walls = new Walls(50, "assets/wall.png");
        root.getChildren().add(walls);
        Scene scene = new Scene(root, 800, 500);
        scene.setOnKeyPressed(this);

        primaryStage.setScene(scene);

        new GameStatus(computerTanks, humenTanks, root);

        Canvas canvas = new Canvas(1300, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawShapes(gc);
        root.getChildren().add(canvas);

        primaryStage.show();
        Computer computer = new Computer(computerTanks, humenTanks, root);
        gameController = new GameController(scene, computer);
        humen = new Humen(humenTanks[0], humenTanks, root, gameController);

        gameController.turnHanlder(TankConst.COMPUTER);
    }

    @Override
    public void handle(KeyEvent event) {
        this.tilePane = null;
        humen.eventHanlder(event);
    }
    
    private void drawShapes(GraphicsContext gc) {
        gc.setLineWidth(0.3);

        // vertical lines
        gc.setStroke(Color.BLUE);
        for (int x = 50; x < 1300; x += 50) {
            gc.strokeLine(x, 50, x, 550);
        }

        //  horizontal lines
        gc.setStroke(Color.RED);
        for (int y = 50; y < 700; y += 50) {
            gc.strokeLine(50, y, 1250, y);
        }

    }

}
