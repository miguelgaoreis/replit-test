package ChaosAI.heuristics.move;

import ontology.Types;

import java.util.ArrayList;

/**
 * Created by Chris on 29.04.2016.
 */
public abstract class MoveHeuristic {
    public Types.ACTIONS getBest(ArrayList<Types.ACTIONS> actions){
        double value = 0;
        Types.ACTIONS newAction = Types.ACTIONS.ACTION_NIL;
        for(Types.ACTIONS ac : actions) {
            double newValue = evaluate(ac);
            if (newValue > value) {
                newAction = ac;
                value = newValue;
            }
        }
        return newAction;
    }
    protected abstract double evaluate(Types.ACTIONS action);
}
