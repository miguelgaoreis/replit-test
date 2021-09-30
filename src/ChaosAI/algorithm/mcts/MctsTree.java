package ChaosAI.algorithm.mcts;

import ChaosAI.algorithm.BaseAlgorithm;
import ChaosAI.algorithm.mcts.roullout.FullyRandomRollout;
import ChaosAI.algorithm.mcts.roullout.RolloutStrategy;
import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.heuristics.move.MoveHeuristic;
import ChaosAI.heuristics.move.RandomMove;
import ChaosAI.utils.GameState;
import ChaosAI.utils.OutOfTimeException;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;

/**
 * Created by Chris on 26.04.2016.
 */
public class MctsTree extends BaseAlgorithm{
    private final int ROLLOUT_DEPTH = 7;

    protected static boolean OPEN_LOOP = true;
    private MctsNode ROOT;
    private RolloutStrategy rolloutStrategy;

    public MctsTree(GameState gameState, HeuristicBase heuristic){
        super(gameState, heuristic);
        ROOT = new MctsNode(gameState);
        rolloutStrategy = new FullyRandomRollout(gameState,heuristic);
    }
    @Override
    public void updateFields(GameState gameState){
        super.updateFields(gameState);
        if(!OPEN_LOOP){
            if(gameState.hashCode() != ROOT.getGameState().hashCode()){
                System.out.println("Detected stochastic event, switching mode!");
                OPEN_LOOP = true;
                ROOT.cleanUpSubTree();
            }
        }
        ROOT.setGameState(gameState);
    }

    public void computeOnce() throws OutOfTimeException{
                MctsNode node = selectNode(ROOT);
                node = expandTree(node);
                double value = simulateFrom(node);
                node.backPropagateValue(value);
    }


    //---------------MCTS-RELATED-MOVES---------------------------------------------------------------------------------


    private MctsNode selectNode(MctsNode node){
        if(node.children.size() < knownMoves.size())
            return node;


        MctsNode newNode = node.children.get(0);
        double value= newNode.getUCT();

        for(MctsNode mctsNode : node.children){
            double uct =  mctsNode.getUCT();
            if(uct > value){
                value = uct;
                newNode = mctsNode;
            }
        }
        return selectNode(newNode);
    }
    private MctsNode expandTree(MctsNode node){
        ArrayList<Types.ACTIONS> possibleMoves = new ArrayList<>(knownMoves);
        for (MctsNode mctsNode : node.children)
            possibleMoves.remove(mctsNode.moveToThisNode);
        //TODO: add expand strategy here
        MoveHeuristic mh = new RandomMove();
        Types.ACTIONS ac = mh.getBest(possibleMoves);
        MctsNode x = node.addChild(ac);
       //FIXME: Remove StateObs of fully advanced node
        if(node.children.size() == possibleMoves.size() && !OPEN_LOOP)
            node.getGameState().removeStateObservation();
        return x;
    }
    private double simulateFrom(MctsNode node) throws OutOfTimeException{
        GameState simulationState = node.getGameState();
        //Random decision on rolloutStrategy
        double stateVale = rolloutStrategy.executeRollout(simulationState);
        return stateVale;
    }

    //--------------HELP-METHODS----------------------------------------------------------------------------------------


    public Types.ACTIONS getNextMove() {
        MctsNode bestNode = ROOT.children.get(0);
        double value = bestNode.getVisits();
        for (MctsNode node : ROOT.children) {
            double newValue = node.getVisits();
            if(newValue > value){
                value =  newValue;
                bestNode = node;
            }
        }
        ROOT = bestNode;
        return bestNode.makeToRoot();
    }
}
