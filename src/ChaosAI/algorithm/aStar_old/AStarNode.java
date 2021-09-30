package ChaosAI.algorithm.aStar_old;

import ChaosAI.utils.*;
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import tools.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Blindguard on 13.05.2016.
 */
public class AStarNode extends CacheableNode implements Comparable<AStarNode>, Node {
    protected AStarNode parent;
    protected AStarNode[] children;
    protected Types.ACTIONS lastAction;
    protected Types.ACTIONS avatarLastAction;
    protected boolean avatarHasOrientation;
    protected boolean rotationAction;
    protected int depth;
    protected double g;
    protected double h;
    protected Position position;

    private static int counter = 0;

    // Factor for weighted A*
    private static final double EPSILON = 3.0;

    public AStarNode(AStarNode parent, StateObservation state, boolean rotationAction,Types.ACTIONS lastAction, GameStateCache gsc) {
        super(state, gsc);
        this.parent = parent;
        this.position = new Position(state.getAvatarPosition(), state.getBlockSize());
        this.children = new AStarNode[state.getAvailableActions().size()];
        this.lastAction = lastAction;
        this.rotationAction = rotationAction;
        this.avatarLastAction = state.getAvatarLastAction();
        avatarHasOrientation = (state.getAvatarOrientation().x != 0 || state.getAvatarOrientation().y != 0);
        this.depth = parent != null ? parent.getDepth() + 1 : 0;
        this.g = (parent != null ? parent.g + AStar.costFunction.evaluate(this, parent.state, this.state) : 0);
        this.h = (parent != null ? - AStar.heuristicFunction.evaluate(this, parent.state, this.state) : - AStar.heuristicFunction.evaluate(this, this.state));
    }

    public boolean isWinningState() {
        return state.isGameOver && state.gameWinner == Types.WINNER.PLAYER_WINS;
    }

    public boolean isLoosingState() {
        return state.isGameOver && state.gameWinner == Types.WINNER.PLAYER_LOSES;
    }

    public AStarNode expandWithAction(Types.ACTIONS a) {
        StateObservation so = this.state.getGameState(this).copy();
        this.state.advance(this, a, so);

        this.children[Util.getIntForAction(a)] = new AStarNode(this, so, false, a, this.gsc);
        return this.children[Util.getIntForAction(a)];
    }

    public AStarNode expandUntilNotOriented(Types.ACTIONS a) {
        StateObservation so = this.state.getGameState(this).copy();
        while(so.getAvatarOrientation().x != 0 || so.getAvatarOrientation().y != 0) {
            this.state.advance(this, Types.ACTIONS.ACTION_NIL, so);
            if(!so.isAvatarAlive()) {
                return null;
            }

            if(so.isGameOver() && so.getGameWinner() == Types.WINNER.PLAYER_WINS) {
                this.children[Util.getIntForAction(a)] = new AStarNode(this, so, false, a, this.gsc);
                return this.children[Util.getIntForAction(a)];
            }
        }
        this.state.advance(this, a, so);

        this.children[Util.getIntForAction(a)] = new AStarNode(this, so, false, a, this.gsc);
        return this.children[Util.getIntForAction(a)];
    }

    public AStarNode expandRotationBased(Types.ACTIONS a) {
        StateObservation so = this.state.getGameState(this).copy();

        Types.ACTIONS directAction = Types.ACTIONS.fromVector(so.getAvatarOrientation());
        if(directAction == a) {
            // walk directly because we are already looking in the right direction
            this.state.advance(this, a, so);
            this.children[Util.getIntForAction(a)] = new AStarNode(this, so, false, a, this.gsc);
        } else {
            // do the action twice, once to turn and once to walk
            this.state.advance(this, a, so);
            this.state.advance(this, a, so);
            this.children[Util.getIntForAction(a)] = new AStarNode(this, so, true, a, this.gsc);
        }

        return this.children[Util.getIntForAction(a)];
    }


    public void updateNode(AStarNode n) {
        this.parent = n.parent;
        this.lastAction = n.lastAction;
        this.depth = n.depth;
        this.g = n.g;
        counter++;
    }

    @Override
    public void backtraceActions(ArrayList<Types.ACTIONS> winPath) {
        if(this.parent != null) {
            winPath.add(this.lastAction);
            if(this.rotationAction)
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

    public Position getPosition() {
        return this.position;
    }

    public StateObservation getGameState() {
        return this.state.getGameState(this);
    }

    @Override
    public String toString() {
        return "" + lastAction + "  |  X: " + this.position.x + "  Y: " + this.position.y;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof AStarNode) {
            AStarNode asn = (AStarNode) o;
            if(asn.getStateHash() == this.getStateHash()) {
                return true;
            }
        }

        return false;
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
