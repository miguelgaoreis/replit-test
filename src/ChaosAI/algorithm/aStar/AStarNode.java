package ChaosAI.algorithm.aStar;

import ChaosAI.utils.*;
import core.game.StateObservation;
import ontology.Types;

import java.util.ArrayList;

/**
 * Created by Blindguard on 13.05.2016.
 */
public class AStarNode implements Comparable<AStarNode>, Node {
    protected AStarNode parent;
    protected AStarNode[] children;
    protected Types.ACTIONS lastAction;
    protected Types.ACTIONS avatarLastAction;
    protected GameState state;
    protected boolean avatarHasOrientation;
    protected int depth;
    protected double g;
    protected double h;

    // Factor for weighted A*
    private static final double EPSILON = 2.5;

    public AStarNode(AStarNode parent, GameState state, Types.ACTIONS lastAction) {
        this.parent = parent;
        this.state = state;
        this.children = new AStarNode[state.getAvailableActions().size()];
        this.lastAction = lastAction;
        this.avatarLastAction = state.getAvatarLastAction();
        avatarHasOrientation = (state.getAvatarOrientation().x != 0 || state.getAvatarOrientation().y != 0);
        this.depth = parent != null ? parent.getDepth() + 1 : 0;
        this.g = (parent != null ? parent.g + AStar.costFunction.evaluate(parent.state, this.state) : 0);
        this.h = (parent != null ? - AStar.heuristicFunction.evaluate(parent.state, this.state) : - AStar.heuristicFunction.evaluate(this.state));
    }

    public boolean isWinningState() {
        return state.isGameOver() && state.getGameWinner() == Types.WINNER.PLAYER_WINS;
    }

    public boolean isLoosingState() {
        return state.isGameOver() && state.getGameWinner() == Types.WINNER.PLAYER_LOSES;
    }

    public AStarNode expandWithAction(Types.ACTIONS a) {
        GameState gs = this.state.copy();
        gs.advance(a);

        this.children[Util.getIntForAction(a)] = new AStarNode(this, gs, a);
        return this.children[Util.getIntForAction(a)];
    }

    public AStarNode expandUntilNotOriented(Types.ACTIONS a) {
        GameState gs = this.state.copy();
        while(gs.getAvatarOrientation().x != 0 || gs.getAvatarOrientation().y != 0) {
            gs.advance(Types.ACTIONS.ACTION_NIL);
            if(!gs.isAvatarAlive()) {
                return null;
            }

            if(gs.isGameOver() && gs.getGameWinner() == Types.WINNER.PLAYER_WINS) {
                this.children[Util.getIntForAction(a)] = new AStarNode(this, gs, a);
                return this.children[Util.getIntForAction(a)];
            }
        }
        gs.advance(a);

        this.children[Util.getIntForAction(a)] = new AStarNode(this, gs, a);
        return this.children[Util.getIntForAction(a)];
    }


    public void updateNode(AStarNode n) {
        this.parent = n.parent;
        this.lastAction = n.lastAction;
        this.depth = n.depth;
        this.g = n.g;
    }

    public void backtraceActions(ArrayList<Types.ACTIONS> winPath) {
        if(this.parent != null) {
            winPath.add(this.lastAction);
            parent.backtraceActions(winPath);
        }
    }

    public boolean greaterGThan(AStarNode n) {
        return n.g < this.g;
    }

    public int getDepth() {
        return this.depth;
    }

    public double getF() {
        return this.g + EPSILON * this.h;
    }

    /*public StateObservation getGameState() {
        return this.state.getGameState(this);
    }*/

    @Override
    public String toString() {
        return "" + lastAction;
    }

    public AStarNode getParent() {
        return this.parent;
    }

    public AStarNode[] getChildren() {
        return this.children;
    }

    public int compareTo(AStarNode n) {
        return Double.compare(this.getF(), n.getF());
    }
}
