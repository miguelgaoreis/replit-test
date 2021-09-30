package ChaosAI.utils;

import ChaosAI.Agent;
import ChaosAI.algorithm.aStar_old.AStar;
import ChaosAI.knowledgebase.KBFood;
import core.game.Event;
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import tools.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by Blindguard on 17.05.2016.
 */
public class CacheableGameState {
    private final int TIMER_LIMIT = 10;
    private static long maxAdvanceDuration = 0;

    private static GameStateCache gsc;
    protected static int blockSize;
    protected static int PRIM = 16777619;
    protected static int INITIAL_VALUE = -2128831035;
    private int hashCode;

    public double gameScore;
    public Vector2d avatarPosition;
    public boolean isGameOver;
    public Types.WINNER gameWinner;

    public CacheableGameState(CacheableNode n, StateObservation so, GameStateCache gsc) {
        this.gsc = gsc;
        this.avatarPosition = so.getAvatarPosition();
        this.blockSize = so.getBlockSize();
        this.gameScore = so.getGameScore();
        this.isGameOver = so.isGameOver();
        this.gameWinner = so.getGameWinner();

        this.calcFNV1aHash(so);
        this.gsc.storeState(n, so, this.hashCode);
    }

    public StateObservation getGameState(CacheableNode n) {
        return this.gsc.getState(n, this.hashCode());
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public void calcFNV1aHash(StateObservation so) {
        int hash = INITIAL_VALUE;

        hash = hashValue(hash, (int) so.getGameScore());
        //hash = hashValue(hash, (int) so.getAvatarSpeed());
        hash = hashValue(hash, (so.isGameOver() ? 1 : 0));
        hash = hashValue(hash, so.getGameWinner().ordinal());
        hash = hashPosition(hash, so.getAvatarPosition());
        //if(AStar.orientationBased)
        //    hash = hashPosition(hash, so.getAvatarOrientation());
        hash = hashObservationList(hash, so.getMovablePositions(so.getAvatarPosition()));
        hash = hashObservationList(hash, so.getImmovablePositions(so.getAvatarPosition()));
        hash = hashObservationList(hash, so.getNPCPositions(so.getAvatarPosition()));
        hash = hashObservationList(hash, so.getResourcesPositions(so.getAvatarPosition()));
        HashMap<Integer, Integer> resources = so.getAvatarResources();
        for(Map.Entry<Integer, Integer> e: resources.entrySet()) {
            hash = hashValue(hash, e.getValue());
            hash = hashValue(hash, e.getKey());
        }
        this.hashCode = hash;
    }

    private int hashValue(int hash, int value) {
        byte b;
        for(int i = 0; i < 8; i++) {
            b = (byte) (value >> (8 - i - 1 << 3));
            hash = hash ^ b;
            hash = hash * PRIM;
        }

        return hash;
    }

    private int hashPosition(int hash, Vector2d vec) {
        if(vec != null) {
            hash = hashValue(hash, (int)(vec.x / blockSize));
            hash = hashValue(hash, (int)(vec.y / blockSize));
        }
        return hash;
    }

    private int hashObservationList(int hash, ArrayList<Observation>[] arrList) {
        if(arrList != null) {
            for(ArrayList<Observation> obsList: arrList) {
                for(Observation obs: obsList) {
                    hash = hashPosition(hash, obs.position);
                    hash = hashValue(hash, obs.itype);
                }
            }
        }
        return hash;
    }

    public void advance(CacheableNode n, Types.ACTIONS action, StateObservation so){
        GameState oldState = null;
        if(Agent.LEARN)
            oldState = new GameState(so);

        Agent.timer.checkAdvanceTimer();
        long timeBefore = Agent.timer.elapsedMillis();
        so.advance(action);
        long timeUsed = Agent.timer.elapsedMillis() - timeBefore;
        Agent.timer.updateOnAdvance(timeUsed);

        if(Agent.LEARN)
            Agent.knowledgeBase.learn(new KBFood(oldState, new GameState(so)));

        GameState.advancesDone++;
    }

    /*-----------------------------------------------------------------------------*/

    public ArrayList<Observation>[] getMoveablePositions(CacheableNode n) {
        return this.gsc.getState(n, this.hashCode).getMovablePositions(this.avatarPosition);
    }

    public TreeSet<Event> getEventHistory(CacheableNode n) {
        return this.gsc.getState(n, this.hashCode()).getEventsHistory();
    }
}
