package ChaosAI.utils;

import ChaosAI.Agent;
import ChaosAI.knowledgebase.KBFood;
import core.game.Event;
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import tools.Vector2d;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import static ChaosAI.Agent.knowledgeBase;

/*
 * WrapperClass for StateObservations
 * Created by Jakob Bruenker on 10.05.2016
 */
public class GameState {

    private int hashCode = 0;
    private StateObservation state;

    public static int totalTimeUsed;
    public static int advancesDone;

    public StateObservation getState() {
      return state;
    }

    private int hashDouble(double d) {
        return Long.valueOf(Double.doubleToLongBits(d)).hashCode();
    }

    private int hashVector2d(Vector2d v) {
        return 122 * (76 * hashDouble(v.x) + hashDouble(v.y));
    }

    private int hashObservation(Observation obs) {
        return 19 * obs.category + obs.itype * 13 + obs.obsID * 7 + hashVector2d(obs.position) + hashVector2d(obs.reference) + hashDouble(obs.sqDist);
    }


    public GameState(StateObservation so) {
        state = so;
        hashCode = 0;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = hashDouble(state.getGameScore());
            hashCode ^= (state.getGameWinner().ordinal() * 17);
            hashCode ^= hashVector2d(state.getAvatarPosition());
            hashCode ^= hashVector2d(state.getAvatarOrientation());

            if (state.getMovablePositions() != null) {
                for (ArrayList<Observation> al : state.getMovablePositions()) {
                    for (Observation obs : al) {
                        hashCode ^= hashObservation(obs);
                    }
                }
            }
            if (state.getFromAvatarSpritesPositions() != null) {
                for (ArrayList<Observation> al : state.getFromAvatarSpritesPositions()) {
                    for (Observation obs : al) {
                        hashCode ^= hashObservation(obs);
                    }
                }
            }
            if (state.getNPCPositions() != null) {
                for (ArrayList<Observation> al : state.getNPCPositions()) {
                    for (Observation obs : al) {
                        hashCode ^= hashObservation(obs);
                    }
                }
            }
        }
        return hashCode;
    }
    public void advance(Types.ACTIONS action){
        GameState oldState = null;
        if(Agent.LEARN)
            oldState = this.copy();

        Agent.timer.checkAdvanceTimer();
        long timeBefore = Agent.timer.elapsedMillis();
        state.advance(action);
        long duration = Agent.timer.elapsedMillis() - timeBefore;
        Agent.timer.updateOnAdvance(duration);

        if(Agent.LEARN)
            knowledgeBase.learn(new KBFood(oldState,this));

        advancesDone++;
    }

    public StateObservation getStateObservation(){
        return state;
    }
    public void removeStateObservation(){state = null;}
    /************************************************************************************/
    public GameState copy(){
        GameState gs = new GameState(state.copy());
        return gs;
    }
    public void setNewSeed(int seed)
    {
        state.setNewSeed(seed);
    }

    /**
     * Returns the actions that are available in this game for
     * the avatar.
     * @return the available actions.
     */
    public ArrayList<Types.ACTIONS> getAvailableActions()
    {
        return state.getAvailableActions(false);
    }

    /**
     * Returns the actions that are available in this game for
     * the avatar. If the parameter 'includeNIL' is true, the array contains the (always available)
     * NIL action. If it is false, this is equivalent to calling getAvailableActions().
     * @param includeNIL true to include Types.ACTIONS.ACTION_NIL in the array of actions.
     * @return the available actions.
     */
    public ArrayList<Types.ACTIONS> getAvailableActions(boolean includeNIL)
    {
        return state.getAvailableActions(includeNIL);
    }


    /**
     * Gets the score of the game at this observation.
     * @return score of the game.
     */
    public double getGameScore()
    {

        return state.getGameScore();
    }

    /**
     * Returns the game tick of this particular observation.
     * @return the game tick.
     */
    public int getGameTick()
    {
        return state.getGameTick();
    }

    /**
     * Indicates if there is a game winner in the current observation.
     * Possible values are Types.WINNER.PLAYER_WINS, Types.WINNER.PLAYER_LOSES and
     * Types.WINNER.NO_WINNER.
     * @return the winner of the game.
     */
    public Types.WINNER getGameWinner()
    {
        return state.getGameWinner();
    }

    /**
     * Indicates if the game is over or if it hasn't finished yet.
     * @return true if the game is over.
     */
    public boolean isGameOver()
    {
        return state.isGameOver();
    }

    /**
     * Returns the world dimensions, in pixels.
     * @return the world dimensions, in pixels.
     */
    public Dimension getWorldDimension()
    {
        return state.getWorldDimension();
    }

    /**
     * Indicates how many pixels form a block in the game.
     * @return how many pixels form a block in the game.
     */
    public int getBlockSize()
    {
        return state.getBlockSize();
    }

    //Methods to retrieve the state of the avatar, in the game...


    /**
     * Returns the position of the avatar. If the game is finished, we cannot guarantee that
     * this position reflects the real position of the avatar (the avatar itself could be
     * destroyed). If game finished, this returns Types.NIL.
     * @return position of the avatar, or Types.NIL if game is over.
     */
    public Vector2d getAvatarPosition()
    {
        return state.getAvatarPosition();
    }

    /**
     * Returns the speed of the avatar. If the game is finished, we cannot guarantee that
     * this speed reflects the real speed of the avatar (the avatar itself could be
     * destroyed). If game finished, this returns 0.
     * @return orientation of the avatar, or 0 if game is over.
     */
    public double getAvatarSpeed()
    {
        return state.getAvatarSpeed();
    }

    /**
     * Returns the orientation of the avatar. If the game is finished, we cannot guarantee that
     * this orientation reflects the real orientation of the avatar (the avatar itself could be
     * destroyed). If game finished, this returns Types.NIL.
     * @return orientation of the avatar, or Types.NIL if game is over.
     */
    public Vector2d getAvatarOrientation() {
        return state.getAvatarOrientation();
    }

    /**
     * Returns the resources in the avatar's possession. As there can be resources of different
     * nature, each entry is a key-value pair where the key is the resource ID, and the value is
     * the amount of that resource type owned. It should be assumed that there might be other resources
     * available in the game, but the avatar could have none of them.
     * If the avatar has no resources, an empty HashMap is returned.
     * @return resources owned by the avatar.
     */
    public HashMap<Integer, Integer> getAvatarResources() {
        return state.getAvatarResources();
    }

    /**
     * Returns the avatar's last move. At the first game cycle, it returns ACTION_NIL.
     * Note that this may NOT be the same as the last action given by the agent, as it may
     * have overspent in the last game cycle.
     * @return the action that was executed in the real game in the last cycle. ACTION_NIL
     * is returned in the very first game step.
     */
    public Types.ACTIONS getAvatarLastAction()
    {
        return state.getAvatarLastAction();
    }

    /**
     * Returns the avatar's type. In case it has multiple types, it returns the most specific one.
     * @return the itype of the avatar.
     */
    public int getAvatarType()
    {
        return state.getAvatarType();
    }

    /**
     * Returns the health points of the avatar. A value of 0 doesn't necessarily
     * mean that the avatar is dead (could be that no health points are in use in that game).
     * @return a numeric value, the amount of remaining health points.
     */
    public int getAvatarHealthPoints() { return state.getAvatarHealthPoints(); }

    /**
     * Returns the maximum amount of health points.
     * @return the maximum amount of health points the avatar ever had.
     */
    public int getAvatarMaxHealthPoints() { return state.getAvatarMaxHealthPoints(); }

    /**
     * Returns the limit of health points this avatar can have.
     * @return the limit of health points the avatar can have.
     */
    public int getAvatarLimitHealthPoints() {return state.getAvatarLimitHealthPoints();}

    /**
     * returns true if the avatar is alive.
     * @return true if the avatar is alive.
     */
    public boolean isAvatarAlive() {return state.isAvatarAlive();}

    //Methods to retrieve the state external to the avatar, in the game...

    /**
     * Returns a grid with all observations in the level, accessible by the x,y coordinates
     * of the grid. Each grid cell has a width and height of getBlockSize() pixels. Each cell
     * contains a list with all observations in that position. Note that the same observation
     * may occupy more than one grid cell.
     * @return the grid of observations
     */
    public ArrayList<Observation>[][] getObservationGrid()
    {
        return state.getObservationGrid();
    }

    /**
     * This method retrieves a list of events that happened so far in the game. In this
     * context, events are collisions of the avatar with other sprites in the game. Additionally,
     * the list also contains information about collisions of a sprite created by the avatar
     * (usually by using the action Types.ACTIONS.ACTION_USE) with other sprites. The list
     * is ordered asc. by game step.
     *
     * @return list of events triggered by the avatar or sprites it created.
     */
    public TreeSet<Event> getEventsHistory()
    {
        return state.getEventsHistory();
    }

    /**
     * Returns a list of observations of NPC in the game. As there can be
     * NPCs of different type, each entry in the array corresponds to a sprite type.
     * Every ArrayList contains a list of objects of type Observation.
     * Each Observation holds the position, unique id and
     * sprite id of that particular sprite.
     *
     * @return Observations of NPCs in the game.
     */
    public ArrayList<Observation>[] getNPCPositions()
    {
        return state.getNPCPositions(null);
    }


    /**
     * Returns a list of observations of NPC in the game. As there can be
     * NPCs of different type, each entry in the array corresponds to a sprite type.
     * Every ArrayList contains a list of objects of type Observation, ordered asc. by
     * distance to the reference passed. Each Observation holds the position, sprite type id and
     * sprite id of that particular sprite.
     *
     * @param reference   Reference position to use when sorting this array,
     *                    by ascending distance to this point.
     * @return Observations of NPCs in the game.
     */
    public ArrayList<Observation>[] getNPCPositions(Vector2d reference)
    {
        return state.getNPCPositions(reference);
    }

    /**
     * Returns a list of observations of immovable sprites in the game. As there can be
     * immovable sprites of different type, each entry in the array corresponds to a sprite type.
     * Every ArrayList contains a list of objects of type Observation.
     * Each Observation holds the position, unique id and
     * sprite id of that particular sprite.
     *
     * @return Observations of immovable sprites in the game.
     */
    public ArrayList<Observation>[] getImmovablePositions() {
        return state.getImmovablePositions(null);
    }

    /**
     * Returns a list of observations of immovable sprites in the game. As there can be
     * immovable sprites of different type, each entry in the array corresponds to a sprite type.
     * Every ArrayList contains a list of objects of type Observation, ordered asc. by
     * distance to the reference passed. Each Observation holds the position, sprite type id and
     * sprite id of that particular sprite.
     *
     * @param reference   Reference position to use when sorting this array,
     *                    by ascending distance to this point.
     * @return Observations of immovable sprites in the game.
     */
    public ArrayList<Observation>[] getImmovablePositions(Vector2d reference) {
        return state.getImmovablePositions(reference);
    }

    /**
     * Returns a list of observations of sprites that move, but are NOT NPCs in the game.
     * As there can be movable sprites of different type, each entry in the array
     * corresponds to a sprite type. Every ArrayList contains a list of objects of type
     * Observation. Each Observation holds the position,
     * unique id and sprite id of that particular sprite.
     *
     * @return Observations of movable, not NPCs, sprites in the game.
     */
    public ArrayList<Observation>[] getMovablePositions() {
        return state.getMovablePositions(null);
    }

    /**
     * Returns a list of observations of movable (not NPCs) sprites in the game. As there can be
     * movable (not NPCs) sprites of different type, each entry in the array corresponds to a sprite type.
     * Every ArrayList contains a list of objects of type Observation, ordered asc. by
     * distance to the reference passed. Each Observation holds the position, sprite type id and
     * sprite id of that particular sprite.
     *
     * @param reference   Reference position to use when sorting this array,
     *                    by ascending distance to this point.
     * @return Observations of movable (not NPCs) sprites in the game.
     */
    public ArrayList<Observation>[] getMovablePositions(Vector2d reference) {
        return state.getMovablePositions(reference);
    }

    /**
     * Returns a list of observations of resources in the game. As there can be
     * resources of different type, each entry in the array corresponds to a sprite type.
     * Every ArrayList contains a list of objects of type Observation.
     * Each Observation holds the position, unique id and
     * sprite id of that particular sprite.
     *
     * @return Observations of resources in the game.
     */
    public ArrayList<Observation>[] getResourcesPositions() {
        return state.getResourcesPositions(null);
    }

    /**
     * Returns a list of observations of resources in the game. As there can be
     * resources of different type, each entry in the array corresponds to a sprite type.
     * Every ArrayList contains a list of objects of type Observation, ordered asc. by
     * distance to the reference passed. Each Observation holds the position, sprite type id and
     * sprite id of that particular sprite.
     *
     * @param reference   Reference position to use when sorting this array,
     *                    by ascending distance to this point.
     * @return Observations of resources in the game.
     */
    public ArrayList<Observation>[] getResourcesPositions(Vector2d reference) {
        return state.getResourcesPositions(reference);
    }

    /**
     * Returns a list of observations of portals in the game. As there can be
     * portals of different type, each entry in the array corresponds to a sprite type.
     * Every ArrayList contains a list of objects of type Observation. Each Observation
     * holds the position, unique id and sprite id of that particular sprite.
     *
     * @return Observations of portals in the game.
     */
    public ArrayList<Observation>[] getPortalsPositions() {

        return state.getPortalsPositions(null);
    }

    /**
     * Returns a list of observations of portals in the game. As there can be
     * portals of different type, each entry in the array corresponds to a sprite type.
     * Every ArrayList contains a list of objects of type Observation, ordered asc. by
     * distance to the reference passed. Each Observation holds the position, sprite type id and
     * sprite id of that particular sprite.
     *
     * @param reference   Reference position to use when sorting this array,
     *                    by ascending distance to this point.
     * @return Observations of portals in the game.
     */
    public ArrayList<Observation>[] getPortalsPositions(Vector2d reference) {
        return state.getPortalsPositions(reference);
    }



    /**
     * Returns a list of observations of sprites created by the avatar (usually, by applying the
     * action Types.ACTIONS.ACTION_USE). As there can be sprites of different type, each entry in
     * the array corresponds to a sprite type. Every ArrayList contains a list of objects of
     * type Observation. Each Observation holds the position, unique id and sprite id
     * of that particular sprite.
     *
     * @return Observations of sprites the avatar created.
     */
    public ArrayList<Observation>[] getFromAvatarSpritesPositions() {

        return state.getFromAvatarSpritesPositions(null);
    }

    /**
     * Returns a list of observations of sprites created by the avatar (usually, by applying the
     * action Types.ACTIONS.ACTION_USE). As there can be sprites of different type, each entry in
     * the array corresponds to a sprite type. Every ArrayList contains a list of objects of
     * type Observation, ordered asc. by distance to the reference passed. Each Observation holds
     * the position, sprite type id and sprite id of that particular sprite.
     *
     * @param reference   Reference position to use when sorting this array,
     *                    by ascending distance to this point.
     * @return Observations of sprites the avatar created.
     */
    public ArrayList<Observation>[] getFromAvatarSpritesPositions(Vector2d reference) {
        return state.getFromAvatarSpritesPositions(reference);
    }

    /**
     * Compares if this and the received StateObservation state are equivalent.
     * DEBUG ONLY METHOD.
     * @param o Object to compare this to.
     * @return true if o has the same components as this.
     */
    public boolean equiv(Object o)
    {
        boolean bool = state.equiv(o);
        //TODO add comparison for gamestate only values here
        return bool;
    }
}
