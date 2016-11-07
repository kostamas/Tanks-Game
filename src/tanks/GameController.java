package tanks;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

public class GameController {

    private Scene mainScene;
    private Computer computer;
    private static int ACTIVE_PLAYER;

    public GameController(Scene mainScene, Computer computer) {
        this.mainScene = mainScene;
        this.computer = computer;
    }

    public static void setActivePlayer(int activePlayer) {
        ACTIVE_PLAYER = activePlayer;
    }
    
    public static int getActivePlayer(){
        return ACTIVE_PLAYER;
    }

    public void turnHanlder(int turn) {
        if (turn == TankConst.COMPUTER) {
            setActivePlayer(TankConst.COMPUTER);
            if(!GameStatus.gameFinished){
                computer.play();
            }
        } else {
            setActivePlayer(TankConst.HUMEN);
        }
    }
}
