package ChaosAI.algorithm.mcts.rewrite;

import ChaosAI.Agent;
import ChaosAI.algorithm.BaseAlgorithm;
import ChaosAI.algorithm.mcts.roullout.FullyRandomRollout;
import ChaosAI.algorithm.mcts.roullout.RolloutStrategy;
import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.GameState;
import ChaosAI.utils.Position;
import ChaosAI.utils.RolloutDrawer;
import ChaosAI.utils.navigation.Pathfinder;
import ontology.Types;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


/**
 * Created by Chris on 24.05.2016.
 */
public class CarloTree extends BaseAlgorithm{

    //TODO set adjusting value;
    private final int ROLLOUT_DEPTH = 7;
    protected static ArrayList<Types.ACTIONS> allPossibleActions;
    private final Position deathPostion = new Position(0,0);
    private static Random rng = new Random();

    private CarloNode root;
    private HeuristicBase heuristic;
    private RolloutStrategy rolloutStrategy;
    private GameState iterationState;

    private int pathStartGameTicks = 0;

    public CarloTree(GameState gameState, HeuristicBase heuristic) {
        super(gameState, heuristic);
        CarloNode.MAX_CHILDREN = this.knownMoves.size();
        CarloTree.allPossibleActions = knownMoves;
        rolloutStrategy = new FullyRandomRollout(gameState,heuristic);
        this.heuristic = heuristic;
        root = CarloNode.generateRoot(gameState);
    }
    /**********************MCTS***Methods*************************************************/
    private CarloNode selectNode(CarloNode node){
        if(node.position.equals(deathPostion)) {
            node.position = new Position(iterationState.getAvatarPosition(),iterationState.getBlockSize());
            node.orientation = new Position(iterationState.getAvatarOrientation(),1);
        }
        if(node.canAddChild())
            return node;

        ArrayList<CarloNode> children = node.getChildren();
        if(children.size() == 0)
            System.out.print("BP");
        CarloNode bestChild = children.get(0);

        for(int i = 1; i < children.size(); i++){
            CarloNode child = children.get(i);
            if(child == null)
                System.out.println("error");
            if(child.getVisits() == 0)
                return child;
            if(bestChild.getUCT() < child.getUCT())
                bestChild = child;
        }
        iterationState.advance(bestChild.getActionToNode());
        if(iterationState.isGameOver())
            return bestChild;
        return selectNode(bestChild);
    }
    private CarloNode expandNode(CarloNode node){
        ArrayList<Types.ACTIONS> remainingActions = node.getRemainingActions();

        Types.ACTIONS nextAction = remainingActions.get(rng.nextInt(remainingActions.size()));
        iterationState.advance(nextAction);

        CarloNode newLeaf = node.addChild(nextAction, iterationState);
        return newLeaf;
    }
    private double simulateFromNode(CarloNode node){
        Random rng = new Random();
        int i;
        double totalScore = 0;
        double e = rng.nextDouble();
        for(i = 0; i < ROLLOUT_DEPTH; i++){
            Position player = new Position(iterationState.getAvatarPosition(),iterationState.getBlockSize());
            ArrayList<Types.ACTIONS> possibleMoves;
            if(e <= 0.5) {
                possibleMoves = this.knownMoves;
            } else {
                possibleMoves = Agent.knowledgeBase.getActionsNotBlockedByWall(player);
            }

            //TODO: possibleMoves is sometimes empty -> nextInt throws exception
            iterationState.advance(possibleMoves.get(rng.nextInt(possibleMoves.size())));
            totalScore += heuristic.evaluate(iterationState);
            if(iterationState.isGameOver())
                return heuristic.evaluate(iterationState);
        }
        return totalScore/ROLLOUT_DEPTH;
    }
    private void backPropagateValue(CarloNode node, double score){
        for(CarloNode iNode = node; iNode != null; iNode = iNode.getParent())
            iNode.increaseScore(score);
    }

    /*********************Algorithm*Specific*Methods*****************************************/

    @Override
    public void updateFields(GameState gameState){
        super.updateFields(gameState);
        Types.ACTIONS lastAction = gameState.getAvatarLastAction();
        ArrayList<CarloNode> children = root.getChildren();

        if(lastAction.equals(Types.ACTIONS.ACTION_NIL) || children.size() == 0 || children.get(0)== null)
            return;
        for(int i = 0; i < children.size(); i++)
            if(children.get(i).getActionToNode().equals(lastAction))
                root = children.get(i);
        root.setRoot();
    }

    @Override
    public void computeOnce() {
        if(Agent.knowledgeBase.currentPath != null) {
            Position avatarPos = new Position(this.currentState.getAvatarPosition(), this.currentState.getBlockSize());
            if(avatarPos.equals(Agent.knowledgeBase.currentPath.get(0)) || this.pathStartGameTicks + 150 == this.currentState.getGameTick()) {
                // add position to already visited targets if we reached it or 100 ticks are over
                Agent.knowledgeBase.visitedTargets.add(Agent.knowledgeBase.currentPath.get(0));
                Agent.knowledgeBase.currentPath = null;
            }
        }

        if(Agent.knowledgeBase.priorityTargets.size() > 0 && Agent.knowledgeBase.wallTypes.size() > 0
                && Agent.knowledgeBase.currentPath == null && this.currentState.getGameTick() >= 200) {
            Position goalPos = new Position(Agent.knowledgeBase.getNextPriorityTarget().position, this.currentState.getBlockSize());

            if(!Agent.knowledgeBase.visitedTargets.contains(goalPos)) {
                Pathfinder pf = new Pathfinder(this.currentState.getStateObservation(), Agent.knowledgeBase.wallTypes, null);
                Position avatarPos = new Position(this.currentState.getAvatarPosition(), this.currentState.getBlockSize());

                Agent.knowledgeBase.currentPath = pf.getPath(avatarPos, goalPos);
            }
        }

        double finalScore;
        iterationState = currentState.copy();
        CarloNode node = selectNode(root);
        if(!iterationState.isGameOver())
            node = expandNode(node);
        if(!iterationState.isGameOver())
            finalScore = simulateFromNode(node);
        else
            finalScore = heuristic.evaluate(iterationState);

        backPropagateValue(node, finalScore);
    }

    @Override
    public Types.ACTIONS getNextMove() {
        ArrayList<CarloNode> children = root.getChildren();
        if(children.size() == 0)
            System.out.print("BP");
        CarloNode bestChild = children.get(0);
        if(bestChild == null) {
            System.out.println("BUUUUUUUUUUUUUG");
            return Types.ACTIONS.ACTION_NIL;
        }
        for(int i = 1; i < children.size(); i++){
            CarloNode child = children.get(i);
            if(child == null)
                return Types.ACTIONS.ACTION_NIL;
            if(bestChild.getVisits() < child.getVisits() || (bestChild.getVisits() == child.getVisits() && bestChild.getTotalScore() < child.getTotalScore()) )
                    bestChild = child;
        }
        return bestChild.getActionToNode();
    }

}
