package ChaosAI.utils.navigation;

import ChaosAI.Agent;
import ChaosAI.utils.Position;
import ChaosAI.utils.RolloutDrawer;
import core.game.Observation;
import core.game.StateObservation;
import tools.Vector2d;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;

//TODO negative heuristic/ point list
/**
 * Created by blindguard on 28/05/16.
 */
public class Pathfinder {
    private StateObservation so;
    private HashMap<Integer, PathNode> allNodes;
    private PriorityQueue<PathNode> openNodes;
    private int width;
    private int height;
    private Integer grid[][];

    /**
     * Pathfinder
     * @param so StateObservation for the game.
     * @param obstacles ArrayList of itypes which represent obstacles.
     */
    public Pathfinder(StateObservation so, ArrayList<Integer> obstacles, ArrayList<Integer> positiveList) {
        ArrayList<Observation>[][] g = so.getObservationGrid();
        this.so = so;
        this.width = g.length;
        this.height =g[0].length;
        this.openNodes = new PriorityQueue<>();
        this.allNodes = new HashMap<>();

        this.grid = new Integer[width][height];
        for(int i=0; i<g.length; i++) {
            for(int j=0; j<g[i].length; j++) {
                grid[i][j] = -1;
                if(g[i][j].size() == 0) {
                    grid[i][j] = 0;
                } else {
                    for (Observation obs : g[i][j]) {
                        if (obstacles != null && !obstacles.contains(obs.itype))
                            grid[i][j] = 0;

                        if(positiveList != null && positiveList.contains(obs.itype))
                            grid[i][j] = 1;
                    }
                }
            }
        }
    }

    /**
     * Finds a path between start and goal.
     * All positions must be discrete block positions.
     * @param start The Vector of the start position.
     * @param goal The Vector of the goal position.
     * @return The ArrayList containing the Path or null if no path was found.
     */
    public ArrayList<Position> getPath(Position start, Position goal) {
        // Initialization
        PathNode init = new PathNode(start, 0.0, calcHeuristics(start, goal));
        openNodes.add(init);
        allNodes.put(init.id, init);

        // Main loop
        PathNode node;
        while(!openNodes.isEmpty())  {
            // get node with lowest cost
            node = openNodes.poll();

            // if this node is the goal node, return the path
            // to this node
            //if(node.position.equals(goal))
            //    return findPath(node);

            // get the possible neighbour nodes
            ArrayList<PathNode> neighbours = getNeighbours(node, goal);
            for(PathNode pN: neighbours) {
                if(!allNodes.containsKey(pN.id)) {
                    // node for this position was not yet seen
                    pN.parent = node;

                    // add the node to the open/all nodes
                    openNodes.add(pN);
                    allNodes.put(pN.id, pN);
                } else {
                    // node was already seen before
                    // get the old node from this position
                    PathNode oldpN = allNodes.get(pN.id);

                    // is the total cost of the old node higher
                    // then that of the new node?
                    if(oldpN.getF() > pN.getF()) {
                        // new node is better
                        pN.parent = node;

                        // remove old node from sets
                        allNodes.remove(oldpN);
                        openNodes.remove(oldpN.id);

                        // add improved node
                        allNodes.put(pN.id, pN);
                        openNodes.add(pN);
                    }
                }
            }
        }

        return findPath(allNodes.get((100*goal.x + goal.y)));
    }

    /**
     * Draws the Game with all Fields colored, that can
     * be part of a path colored:
     * Red = possible path
     * Green = possible path with positive effects
     */
    public void drawGrid() {
        int posCounter = 0;
        int valCounter = 0;
        ArrayList<Position> posP = new ArrayList<>();
        ArrayList<Position> posV = new ArrayList<>();
        for(int i=0; i<grid.length; i++) {
            for(int j=0; j<grid[i].length; j++) {
                if(grid[i][j] == 0) {
                    posCounter++;
                    posP.add(new Position(i, j));
                }

                if(grid[i][j] == 1) {
                    valCounter++;
                    posV.add(new Position(i, j));
                }

            }
        }
        System.out.println("\nPossible nodes: " + posCounter);
        System.out.println("Value nodes: " + valCounter);

        RolloutDrawer rd = new RolloutDrawer(this.so);
        HashMap<Color, ArrayList<Position>> pathMap = new HashMap<>();
        pathMap.put(new Color(255, 0, 0, 125), posP);
        pathMap.put(new Color(0, 255, 0, 125), posV);
        rd.drawPath(pathMap);
        //JOptionPane.showMessageDialog(Agent.frame, rd, "Possible nodes", JOptionPane.DEFAULT_OPTION);
    }

    private double calcHeuristics(Position start, Position end) {
        double xDiff = Math.abs(start.x - end.x);
        double yDiff = Math.abs(start.y - end.y);
        return (xDiff + yDiff) - 5 * grid[start.x][start.y];
    }

    private ArrayList<Position> findPath(PathNode n) {
        ArrayList<Position> positions = new ArrayList<>();
        PathNode it = n;

        while(it.parent != null) {
            positions.add(it.position);
            it = it.parent;
        }

        return positions;
    }

    private ArrayList<PathNode> getNeighbours(PathNode n, Position goal) {
        ArrayList<PathNode> neighbours = new ArrayList<>();
        int x = n.position.x;
        int y = n.position.y;
        Position v;

        if(x-1 >= 0 && grid[x-1][y] >= 0) {
            v = new Position(x - 1, y);
            neighbours.add(new PathNode(v, n.g + 0.1, calcHeuristics(v, goal)));
        }

        if(y-1 >= 0 && grid[x][y-1] >= 0) {
            v = new Position(x, y-1);
            neighbours.add(new PathNode(v, n.g + 0.1, calcHeuristics(v, goal)));
        }

        if(x+1 < width && grid[x+1][y] >= 0) {
            v = new Position(x + 1, y);
            neighbours.add(new PathNode(v, n.g + 0.1, calcHeuristics(v, goal)));
        }

        if(y+1 < height && grid[x][y+1] >= 0) {
            v = new Position(x, y + 1);
            neighbours.add(new PathNode(v, n.g + 0.1, calcHeuristics(v, goal)));
        }

        return neighbours;
    }
}
