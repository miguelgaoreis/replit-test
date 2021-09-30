package ChaosAI.heuristics.evaluation;

import core.game.StateObservation;

/**
 * Created by Chris on 29.04.2016.
 */
public abstract class StateEvaluationHeuristic {
    public abstract double evaluate(StateObservation so);
}
