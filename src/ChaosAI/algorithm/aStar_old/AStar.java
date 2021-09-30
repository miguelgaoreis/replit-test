package ChaosAI.algorithm.aStar_old;

import ChaosAI.Agent;
import ChaosAI.algorithm.BaseAlgorithm;
import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.*;
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import tools.Vector2d;

import javax.swing.*;
import java.util.*;
import java.math.*;

/**
 * Created by Blindguard on 13.05.2016.
 */
public class AStar extends BaseAlgorithm {
    private AStarNode root;
    private PriorityQueue<AStarNode> queue = new PriorityQueue<>();
    private HashMap<Integer, AStarNode> allNodes = new HashMap<>();

    private boolean finished = false;
    private ArrayList<Types.ACTIONS> winPath = new ArrayList<>();

    public static boolean orientationBased;
    public static boolean rotationBased;
    protected static GameStateCache cache;
    protected static AStarHeuristic heuristicFunction;
    protected static AStarCostFunction costFunction;
    protected boolean lastStepMoves = false;

    HashMap<Integer, StateObservation> debugMap = new HashMap<>();

    public AStar(GameState gs, HeuristicBase h) {
        super(gs, h);
        this.rotationBasedCheck(gs);
        orientationBased = (gs.getAvatarOrientation().x != 0 || gs.getAvatarOrientation().y != 0);
        this.cache = new GameStateCache(gs);
        this.heuristicFunction = new AStarHeuristic();
        this.costFunction = new AStarCostFunction();
        AStarNode init = new AStarNode(null, gs.getStateObservation(), false, null, cache);
        queue.add(init);
        allNodes.put(init.getStateHash(), init);
        this.root = init;
    }

    @Override
    public void updateFields(GameState gs) {
        super.updateFields(gs);
        this.evaluationHeuristic.updateFields(gs);
        this.currentState = gs;
    }

    public void computeOnce() {
        if(!finished) {
            if(queue.isEmpty()) {
                if(Agent.DEBUG)
                    System.out.println("Queue is empty. Something went wrong.");
                throw new OutOfTimeException(0);
            }
            AStarNode n = queue.poll();

            if (n.isWinningState()) {
                finished = true;
                n.backtraceActions(winPath);
                Collections.reverse(winPath);
                /*RolloutDrawer rd = new RolloutDrawer(this.currentState.getStateObservation());
                rd.drawRollout(this.winPath);
                JOptionPane.showMessageDialog(Agent.frame, rd, "Winning Path", JOptionPane.DEFAULT_OPTION);
                TreeWriter td = new TreeWriter(this.root);*/
            } else {
                // expand the node n with all available actions
                for (int i = 0; i < knownMoves.size(); i++) {
                    AStarNode next;
                    if(!orientationBased && n.avatarHasOrientation)
                        next = n.expandUntilNotOriented(knownMoves.get(i));
                    else if(rotationBased)
                        next = n.expandRotationBased(knownMoves.get(i));
                    else
                        next = n.expandWithAction(knownMoves.get(i));
                    // don't add nodes with loosing states to the queue
                    if (next == null || next.isLoosingState())
                        continue;

                    int hash = next.getStateHash();
                    // check if a node with the state has been seen before
                    if (allNodes.containsKey(hash)) {
                        // node with that state has already been seen
                        // get that node from the map
                        AStarNode nextOld = allNodes.get(hash);

                        // check if the new node has a lower cost than the old one
                        if (nextOld.greaterGThan(next)) {
                            // updateOnCompute the old node with the new information
                            nextOld.updateNode(next);
                            // than add this node back to the queue
                            queue.remove(nextOld);
                            queue.add(nextOld);
                        }
                    } else {
                        // add the node to the queue
                        queue.add(next);
                        allNodes.put(hash, next);
                    }
                }
            }
        } else {
            throw new OutOfTimeException(0);
        }
    }

    private void rotationBasedCheck(GameState gs) {
        if(gs.getAvatarOrientation().x == 0 && gs.getAvatarOrientation().y == 0) {
            this.rotationBased = false;
            return;
        }

        Vector2d orientation = gs.getAvatarOrientation();
        Types.ACTIONS moveAction = Types.ACTIONS.fromVector(orientation);
        Types.ACTIONS revMoveAction = Types.ACTIONS.fromVector(orientation.mul(-1));
        GameState tmp = gs.copy();

        tmp.advance(moveAction);
        Position oldPosition = new Position(tmp.getAvatarPosition(), gs.getBlockSize());

        tmp.advance(revMoveAction);
        Position newPosition = new Position(tmp.getAvatarPosition(), gs.getBlockSize());

        this.rotationBased = oldPosition.equals(newPosition);
    }

    public Types.ACTIONS getNextMove() {
        if(this.finished) {
            Vector2d orientation = currentState.getAvatarOrientation();
            if(!orientationBased && (orientation.x != 0 || orientation.y != 0)) {
                if(Agent.DEBUG)
                    System.out.println("Avatar is flying. Return NIL action.");
                return Types.ACTIONS.ACTION_NIL;
            } else {
                if(!winPath.isEmpty()) {
                    if(Agent.DEBUG)
                        System.out.println("Action taken: " + this.winPath.get(0));
                    this.lastStepMoves = false;
                    return this.winPath.remove(0);
                } else {
                    if(Agent.DEBUG)
                        System.out.println("WinPath is empty and no terminal was reached. Something's fucky...");
                    return Types.ACTIONS.ACTION_NIL;
                }
            }
        } else {
            return Types.ACTIONS.ACTION_NIL;
        }
    }
}
