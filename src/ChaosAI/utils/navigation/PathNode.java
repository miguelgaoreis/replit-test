package ChaosAI.utils.navigation;

import ChaosAI.utils.Position;
import tools.Vector2d;

/**
 * Created by blindguard on 28/05/16.
 */
public class PathNode implements Comparable<PathNode> {
    public Position position;
    public PathNode parent;
    public int id;
    public double g;
    public double h;

    public PathNode(Position pos, double g, double h) {
        this.g = g;
        this.h = h;
        this.position = pos;
        this.id = pos.x * 100 + pos.y;
    }

    public double getF() {
        return this.g + this.h;
    }

    public int compareTo(PathNode n) {
        if(n.getF() < this.getF())
            return 1;
        else if(n.getF() > this.getF())
            return -1;
        else
            return 0;
    }
}
