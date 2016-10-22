package tanks;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

public class GameController {

    private Scene mainScene;
    private Computer computer;
    private EventHandler<? super KeyEvent> humenEventHandler;
    
    private final int COMPUTER_TURN = 0;
    private final int HUMEN_TURN = 0;

    public GameController(Scene mainScene, Computer computer,EventHandler<? super KeyEvent> humenEventHandler) {
        this.mainScene = mainScene;
        this.computer = computer;
        this.humenEventHandler = humenEventHandler;
    }

    public void turnHanlder(int turn) {
        if (turn == COMPUTER_TURN) {
            computer.play();
            this.turnHanlder(1);
        } else {
            mainScene.setOnKeyPressed((EventHandler<? super KeyEvent>) humenEventHandler);
        }
    }

}
