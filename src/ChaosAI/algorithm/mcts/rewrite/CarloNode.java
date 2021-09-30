package ChaosAI.algorithm.mcts.rewrite;

import ChaosAI.Agent;
import ChaosAI.utils.GameState;
import ChaosAI.utils.Position;
import ontology.Types;
import tools.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Chris on 24.05.2016.
 */
public class CarloNode{
    private final static double[] bounds = new double[]{Double.MAX_VALUE, -Double.MAX_VALUE};
    private final static double epsilon = 1e-6;
    private final static Random rng = new Random();
    private final static double EXPLORATION_VALUE = Math.sqrt(2);
    public static int MAX_CHILDREN = 1;
    public static double Q = 0.1;

    private boolean isRoot = false;

    private final Types.ACTIONS action;
    private long visits = 0;
    private double totalScore = 0;
    private double maxScore = 0;

    private CarloNode parent;
    private ArrayList<CarloNode> children;
    private ArrayList<Types.ACTIONS> possibleMoves;


    protected Position position;
    protected Position orientation;

    private CarloNode(CarloNode parent, Types.ACTIONS action, GameState resultingState){
        this.parent = parent;
        this.action = action;
        this.children = new ArrayList<>(MAX_CHILDREN);
        this.possibleMoves = new ArrayList<>(CarloTree.allPossibleActions);
        this.position = new Position(resultingState.getAvatarPosition(),resultingState.getBlockSize());
        this.orientation = new Position(resultingState.getAvatarOrientation(),1);
    }
    /***************USAGE*Methods******************************/
    public Types.ACTIONS getActionToNode(){
        return action;
    }
    /***************MCTS Specific Methods****************/
    public double getUCT(){
        double avgScore = totalScore/(visits);
        double mixMaxScore = Q * maxScore + (1 - Q) * avgScore;
        double expl = EXPLORATION_VALUE * Math.sqrt(Math.log(parent.visits)/(visits));
        //if(!this.orientation.equals(parent.orientation))
        //    expl *= 0.995;
        double uctValue = mixMaxScore + expl;
        //uctValue = Utils.noise(uctValue, this.epsilon, this.rng.nextDouble());     //break ties randomly
        //if(!this.orientation.equals(parent.orientation))
        //    uctValue *= 0.99; //Might need to be changed!
        return uctValue;
    }
    public void increaseScore(double score){
        if(score > this.maxScore)
            this.maxScore = score;
        this.totalScore += score;
        visits++;
    }
    /********************Tree Methods******************/
    public CarloNode addChild(Types.ACTIONS action, GameState nextGameState){
        possibleMoves.remove(action);
        CarloNode c = new CarloNode(this,action,nextGameState);
        children.add(c);
        return c;
    }
    public CarloNode getParent(){
        if(isRoot)
            return null;
        return parent;
    }
    public ArrayList<CarloNode> getChildren(){
        return children;
    }
    protected  void setRoot(){
        parent = null;
        isRoot = true;
    }
    public boolean canAddChild(){
        possibleMoves = Agent.knowledgeBase.getActionsNotBlockedByWall(position,possibleMoves);
        return !this.possibleMoves.isEmpty();
    }
    public static CarloNode generateRoot(GameState rootState){
        CarloNode root = new CarloNode(null,null,rootState);
        root.setRoot();
        return root;
    }

    public long getVisits() {
        return visits;
    }
    @Override
    public String toString(){
        return action + " v: "+ visits+"\t| avg: "+getUCT();
    }
    public double getTotalScore(){
        return totalScore;
    }
    public ArrayList<Types.ACTIONS> getRemainingActions() {
        return this.possibleMoves;
    }
}
