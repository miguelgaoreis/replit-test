package ChaosAI.algorithm.bfs;

import ChaosAI.utils.GameState;
import core.game.Game;
import core.game.Observation;
import ontology.Types;

import java.util.ArrayList;

/**
 * Created by blindguard on 19/06/16.
 */
public class BFSNode implements Comparable<BFSNode> {
    private BFSNode parent;
    private BFSNode[] children;
    protected int depth;
    protected double value;
    protected GameState state;
    protected Types.ACTIONS lastAction;

    public BFSNode(BFSNode parent, GameState gs, int depth, Types.ACTIONS lastAction) {
        this.parent = parent;
        this.depth = depth;
        this.state = gs;
        this.value = BestFirstSearch.heuristics.evaluate(gs);
        this.lastAction = lastAction;
        this.children = new BFSNode[BestFirstSearch.NUM_ACTIONS];
    }

    public BFSNode getChildren(int i) {
        return this.children[i];
    }

    public void backtraceActions(ArrayList<Types.ACTIONS> winPath) {
        if(this.parent != null) {
            winPath.add(this.lastAction);
            parent.backtraceActions(winPath);
        }
    }

    boolean isWinnigState() {
        return (this.state.isGameOver() && this.state.getGameWinner() == Types.WINNER.PLAYER_WINS);
    }

    void createChildren() {
        for(int i = 0; i < BestFirstSearch.NUM_ACTIONS; i++) {
            GameState gs = this.state.copy();
            gs.advance(BestFirstSearch.actions[i]);
            this.children[i] = new BFSNode(this, gs, depth + 1, BestFirstSearch.actions[i]);
        }
    }

    void expandWithAction(Types.ACTIONS a, int i) {
        GameState gs = this.state.copy();
        gs.advance(a);
        this.children[i] = new BFSNode(this, gs, this.depth+1, a);
    }

    @Override
    public int hashCode() {
        return this.state.hashCode();
    }

    public int compareTo(BFSNode node) {
        if(node.value > this.value)
            return -1;
        else if(node.value < this.value)
            return 1;
        else
            return 0;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof BFSNode) {
            BFSNode n = (BFSNode) o;
            if(n.state.hashCode() == this.state.hashCode())
                return true;
        }

        return false;
    }
}
