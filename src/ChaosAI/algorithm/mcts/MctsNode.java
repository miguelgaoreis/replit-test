package ChaosAI.algorithm.mcts;

import ChaosAI.utils.GameState;
import ChaosAI.utils.OutOfTimeException;
import ontology.Types;

import java.util.ArrayList;

/**
 * Created by Chris on 26.04.2016.
 */
class MctsNode {
    private final double EXPLORE_FACTOR = Math.sqrt(2);
    private GameState gameState = null;

    private MctsNode parent;
    protected Types.ACTIONS moveToThisNode;
    protected ArrayList<MctsNode> children = new ArrayList<>();

    private int visits = 0;
    private double totalValue = 0;

    public MctsNode(GameState state){
        this.parent = null;
        this.moveToThisNode = null;
        this.gameState = state;
    }
    private MctsNode(MctsNode parent,Types.ACTIONS move){
        this.parent = parent;
        this.moveToThisNode = move;
    }
    protected MctsNode addChild(Types.ACTIONS move){
        MctsNode child = new MctsNode(this,move);
        if(!MctsTree.OPEN_LOOP) {
            GameState childState = gameState.copy();
            childState.advance(move);
            child.setGameState(childState);
        }
        children.add(child);
        return child;
    }

    protected double getUCT(){
        if(parent == null)
            throw new IllegalStateException("Parent Unknown Problem Node: " + this.toString());

        double vi = Math.log(parent.visits);
        double ni = visits;
        double visitRate = Math.sqrt(vi/ni);
        double visitExpl = visitRate * EXPLORE_FACTOR;
        double avgValue = totalValue / ni;

        double uct = avgValue + visitExpl ;
        //double uct = totalValue/visits + EXPLORE_FACTOR * Math.sqrt(Math.log(parent.visits)/(visits*5));
        return uct;
    }
    protected void backPropagateValue(double value){
        this.visits += 1;
        this.totalValue += value;
        if(parent!=null)
            parent.backPropagateValue(value);
    }

    public GameState getGameState(){
        if(MctsTree.OPEN_LOOP){
            return recursiveGameStateCreation();
        } else{
            return gameState;
        }
    }
    public void setGameState(GameState gameState){
        this.gameState = gameState;
    }
    public void cleanUpSubTree(){
        this.cleanGameStateRec();
    }
    //-------------------HELP-FUNCTIONS---------------------------------------------------------------------------------
    protected ArrayList<Types.ACTIONS> getMoveSetToNode(){
        if(parent==null)
            return new ArrayList<>();
        ArrayList<Types.ACTIONS> movesTillParent = parent.getMoveSetToNode();
        movesTillParent.add(moveToThisNode);
        return movesTillParent;
    }
    private GameState recursiveGameStateCreation(){
        if (!MctsTree.OPEN_LOOP)
            throw new IllegalStateException("Function should never be called in ClosedLoop MCTS.");
        GameState newState = gameState;
        if (newState == null)
            newState = parent.recursiveGameStateCreation();
        else
            return newState.copy();
        //if(newState.getGameWinner() == Types.WINNER.PLAYER_LOSES)
        //    return newState;
        newState.advance(moveToThisNode);
        return newState;
    }
    private void cleanGameStateRec(){
        if(moveToThisNode != null)
            gameState = null;
        for(MctsNode child : children)
            child.cleanGameStateRec();
    }

    public Types.ACTIONS makeToRoot() {
        this.parent = null;
        Types.ACTIONS moveToHere = this.moveToThisNode;
        moveToThisNode = null;
        return moveToHere;
    }

    public int getVisits() {
        return visits;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(parent==null?"Root":moveToThisNode.toString()).append("->Node[vi_").append(visits).append("|sc_").append(totalValue).append("|uct_").append(getUCT()).append("]");
        return sb.toString();
    }
}
