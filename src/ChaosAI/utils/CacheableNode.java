package ChaosAI.utils;

import core.game.StateObservation;
import ontology.Types;

import java.util.ArrayList;

/**
 * Created by Blindguard on 17.05.2016.
 */
public abstract class CacheableNode {
    protected CacheableGameState state;
    protected static GameStateCache gsc;

    public CacheableNode(StateObservation so, GameStateCache gsc) {
        this.gsc = gsc;
        this.state = new CacheableGameState(this, so, gsc);
    }

    /**
     * Returns the hashCode of the GameState saved in this node.
     * @return hashCode of the GameState
     */
    public int getStateHash() {
        return this.state.hashCode();
    }


    /**
     * Adds Types.ACTIONS that lead to the GameState in this Node
     * to the given ArrayList. The Actions should be in reverse order, beginning
     * with the lastAction from this Node.
     * @param path The ArrayList that receives the actions.
     */
    public abstract void backtraceActions(ArrayList<Types.ACTIONS> path);
}
