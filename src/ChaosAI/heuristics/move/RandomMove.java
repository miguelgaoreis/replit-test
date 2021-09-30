package ChaosAI.heuristics.move;

import ontology.Types;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Chris on 29.04.2016.
 */
public class RandomMove extends MoveHeuristic{
    private Random rng = new Random();
    @Override
    public double evaluate(Types.ACTIONS action) {

        return rng.nextDouble();
    }
}
