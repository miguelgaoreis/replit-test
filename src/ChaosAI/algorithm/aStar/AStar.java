package ChaosAI.algorithm.aStar;

import ChaosAI.algorithm.BaseAlgorithm;
import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.GameState;
import ChaosAI.utils.GameStateCache;
import ChaosAI.utils.OutOfTimeException;
import core.game.StateObservation;
import ontology.Types;
import tools.Vector2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Created by Blindguard on 13.05.2016.
 */
public class AStar extends BaseAlgorithm {
    private PriorityQueue<AStarNode> queue = new PriorityQueue<>();
    private HashMap<Integer, AStarNode> allNodes = new HashMap<>();
    //private HashMap<Integer, StateObservation> duplicateStates = new HashMap<>();
    private boolean finished = false;
    private AStarNode root;
    public static boolean orientationBased;
    private ArrayList<Types.ACTIONS> winPath = new ArrayList<>();
    protected static GameStateCache cache;
    protected static AStarHeuristic heuristicFunction;
    protected static AStarCostFunction costFunction;

    public AStar(GameState gs, HeuristicBase h) {
        super(gs, h);
        orientationBased = (gs.getAvatarOrientation().x != 0 || gs.getAvatarOrientation().y != 0);
        this.cache = new GameStateCache(gs);
        this.heuristicFunction = new AStarHeuristic();
        this.costFunction = new AStarCostFunction();
        AStarNode init = new AStarNode(null, gs, null);
        queue.add(init);
        allNodes.put(init.hashCode(), init);
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
                System.out.println("Queue is empty. Something went wrong.");
                //TreeDrawer td = new TreeDrawer(this.root);
                System.exit(-1);
            }
            AStarNode n = queue.poll();

            if (n.isWinningState()) {
                finished = true;
                n.backtraceActions(winPath);
                Collections.reverse(winPath);
                //TreeDrawer td = new TreeDrawer(this.root);
            } else {
                // expand the node n with all available actions
                for (int i = 0; i < knownMoves.size(); i++) {
                    AStarNode next;
                    if(!orientationBased && n.avatarHasOrientation)
                        next = n.expandUntilNotOriented(knownMoves.get(i));
                    else
                        next = n.expandWithAction(knownMoves.get(i));
                    // don't add nodes with loosing states to the queue
                    if (next == null || next.isLoosingState())
                        continue;

                    int hash = next.hashCode();
                    // check if a node with the state has been seen before
                    if (allNodes.containsKey(hash)) {
                        //duplicateStates.put(hash, next.getGameState());
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

    public Types.ACTIONS getNextMove() {
        if(this.finished) {
            Vector2d orientation = currentState.getAvatarOrientation();
            if(!orientationBased && orientation.x != 0 || orientation.y != 0) {
                System.out.println("Avatar is flying. Return NIL action.");
                return Types.ACTIONS.ACTION_NIL;
            } else {
                if(!winPath.isEmpty()) {
                    System.out.println("Action taken: " + this.winPath.get(0));
                    return this.winPath.remove(0);
                } else {
                    System.out.println("WinPath is empty and no terminal was reached. Something's fucky...");
                    //TreeDrawer td = new TreeDrawer(this.root);
                    System.exit(-1);
                    return Types.ACTIONS.ACTION_NIL;
                }
            }
        } else {
            return Types.ACTIONS.ACTION_NIL;
        }
    }
}
