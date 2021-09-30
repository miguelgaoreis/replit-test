package ChaosAI.utils;

import ontology.Types;
import tools.Vector2d;

/**
 * Created by Chris on 12.05.2016.
 */
public class Util {

    //Normalizes a value between its MIN and MAX.
    public static double normalise(double a_value, double a_min, double a_max)
    {
        if(a_min < a_max)
            return (a_value - a_min)/(a_max - a_min);
        else    // if bounds are invalid, then return same value
            return a_value;
    }

    public static double euclideanDistance(Vector2d p, Vector2d q) {
        return Math.sqrt(sqr(q.x - p.x) + sqr(q.y - p.y));
    }

    public static double sqr(double x) {
        return x * x;
    }

    public static double noise(double input, double epsilon, double random)
    {
        if(input != -epsilon) {
            return (input + epsilon) * (1.0 + epsilon * (random - 0.5));
        }else {
            //System.out.format("Utils.tiebreaker(): WARNING: value equal to epsilon: %f\n",input);
            return (input + epsilon) * (1.0 + epsilon * (random - 0.5));
        }
    }

    public static int getIntForAction(Types.ACTIONS a) {
        if(a == Types.ACTIONS.ACTION_DOWN)
            return 0;
        else if(a == Types.ACTIONS.ACTION_UP)
            return 1;
        else if(a == Types.ACTIONS.ACTION_LEFT)
            return 1;
        else if(a == Types.ACTIONS.ACTION_RIGHT)
            return 2;
        else if(a == Types.ACTIONS.ACTION_USE)
            return 0;
        else if(a == Types.ACTIONS.ACTION_ESCAPE)
            return 5;
        else
            return 6;
    }

    public static int manhattanDistance(Position p1, Position p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }
}
