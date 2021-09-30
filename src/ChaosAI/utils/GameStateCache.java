package ChaosAI.utils;

import core.game.StateObservation;
import ontology.Types;

import java.util.*;

/**
 * Created by Blindguard on 17.05.2016.
 */
public class GameStateCache {
    private int capacity = 500;
    private HashMap<Integer, StateObservation> cache;
    private Queue<CacheableNode> inputOrder;
    public static StateObservation ROOT_STATE;

    private final Runtime runtime;
    private static int CLEAR_AMOUNT = 250;
    private int timesCleared = 0;

    public GameStateCache(GameState st) {
        this.ROOT_STATE = st.getStateObservation();

        this.runtime = Runtime.getRuntime();
        this.inputOrder =new LinkedList<>();
        this.cache = new HashMap<>();
    }

    public StateObservation getState(CacheableNode n, int stateHash) {
        if (cache.containsKey(stateHash)) {
            // State is cached, just return it
            return cache.get(stateHash);
        } else {
            // State is not cached, rebuild it
            ArrayList<Types.ACTIONS> act = new ArrayList<>();
            StateObservation so;
            n.backtraceActions(act);
            Collections.reverse(act);
            so = this.rebuildState(act);
            this.storeState(n, so, stateHash);
            return so;
        }
    }

    public void storeState(CacheableNode n, StateObservation so, int stateHash) {
        if(cache.size() + 1 > this.capacity) {
            this.atFullCapacity();
        }

        // Add state to cache if not already in it
        if(!cache.containsKey(stateHash)) {
            cache.put(stateHash, so);
            // Add node to the top of the input stack
            inputOrder.add(n);
        }
    }

    private void atFullCapacity() {
        if(runtime.maxMemory() - runtime.freeMemory() < 0.6 * runtime.maxMemory()) {
            capacity += 100;
            CLEAR_AMOUNT += 50;
        } else {
            for(int i=0; i<CLEAR_AMOUNT; i++) {
                // Poll the node with the oldest input time
                CacheableNode r = this.inputOrder.poll();
                if(r != null)
                    cache.remove(r.getStateHash());
            }
            this.timesCleared++;
        }
    }

    /**
     * Rebuilds the StateObservation of this GameState using the given actions.
     * The actions in the List should be in order from the ROOT_STATE.
     * An instance of GameStateCache must be initialized in order to use this, since
     * it utilizes the ROOT_STATE field of GameStateCache.
     * @param actions List of Types.ACTIONS that are used to rebuild the State.
     * @return The rebuild StateObservation
     */
    private StateObservation rebuildState(ArrayList<Types.ACTIONS> actions) {
        StateObservation state = this.ROOT_STATE.copy();

        for(Types.ACTIONS a: actions) {
            state.advance(a);
        }

        return state;
    }
}
