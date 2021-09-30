package ChaosAI.knowledgebase;

import ChaosAI.utils.GameState;
import core.game.StateObservation;

/**
 * Created by Chris on 10.05.2016.
 */
public class KBFood {
    final GameState oldState;
    final GameState newState;
    public KBFood(GameState oldState, GameState newState){
        this.oldState = oldState;
        this.newState = newState;
    }
}
