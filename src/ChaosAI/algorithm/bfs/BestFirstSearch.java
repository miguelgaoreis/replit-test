package ChaosAI.algorithm.bfs;

import ChaosAI.algorithm.BaseAlgorithm;
import ChaosAI.heuristics.HeuristicBase;
import ChaosAI.utils.GameState;
import ChaosAI.utils.OutOfTimeException;
import ontology.Types;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by blindguard on 19/06/16.
 */
public class BestFirstSearch extends BaseAlgorithm {
    private BFSNode root;
    private HashMap<Integer, BFSNode> closedNodes = new HashMap<>();
    private PriorityQueue<BFSNode> openNodes = new PriorityQueue<>();
    private ArrayList<BFSNode> allNodes = new ArrayList<>();
    protected static HeuristicBase heuristics;
    private ArrayList<Types.ACTIONS> winPath = new ArrayList<>();
    private boolean winPathFound = false;

    protected static Types.ACTIONS[] actions;
    protected static int NUM_ACTIONS;

    public BestFirstSearch(GameState gs, HeuristicBase h) {
        super(gs, h);
        // known moves to static array
        ArrayList<Types.ACTIONS> act = gs.getAvailableActions();
        actions = new Types.ACTIONS[act.size()];
        for(int i = 0; i < actions.length; ++i)
        {
            actions[i] = act.get(i);
        }
        NUM_ACTIONS = actions.length;

        this.heuristics = h;
        this.root = new BFSNode(null, gs, 0, null);
        this.openNodes.add(this.root);
    }

    @Override
    public void updateFields(GameState gameState) {
        super.updateFields(gameState);
        // handle memory overload?
        // handle too little time left?
    }

    @Override
    public void computeOnce() {
        if(!this.winPathFound) {
            if (!openNodes.isEmpty()) {
                // 1. Remove the best node from OPEN, call it n, add it to CLOSED.
                BFSNode n = this.openNodes.poll();
                this.closedNodes.put(n.hashCode(), n);
                // 2. If n is the goal state, backtrace path to n (through recorded parents) and return path.
                if (n.isWinnigState()) {
                    n.backtraceActions(this.winPath);
                    Collections.reverse(this.winPath);
                    this.winPathFound = true;
                } else {
                    // 3. Create n's successors.
                    n.createChildren();
                    // 4. For each successor do:
                    for (int i = 0; i < NUM_ACTIONS; i++) {
                        BFSNode c = n.getChildren(i);
                        //a. If it is not in CLOSED and it is not in OPEN: evaluate it, add it to OPEN, and record its parent.
                        if (!closedNodes.containsValue(c) && !openNodes.contains(c)) {
                            openNodes.add(c);
                            allNodes.add(c);
                        } else {
                            // b. Otherwise, if this new path is better than previous one, change its recorded parent.
                            int j = allNodes.indexOf(c);
                            if (j != -1 && allNodes.get(j).depth > c.depth) {
                                // i.  If it is not in OPEN add it to OPEN.
                                // ii. Otherwise, adjust its priority in OPEN using this new evaluation.
                                openNodes.remove(c);
                                openNodes.add(c);
                            }
                        }
                    }
                }
            } else {
                System.out.println("Queue is empty. Something went wrong.");
            }
        } else {
            throw new OutOfTimeException(0);
        }
    }

    @Override
    public Types.ACTIONS getNextMove() {
        if(winPathFound) {
            if(!winPath.isEmpty()) {
                System.out.println("Action taken: " + this.winPath.get(0));
                return this.winPath.remove(0);
            } else {
                System.out.println("WinPath is empty and no terminal was reached. Something's fucky...");
                return Types.ACTIONS.ACTION_NIL;
            }
        } else {
            return Types.ACTIONS.ACTION_NIL;
        }
    }
}
