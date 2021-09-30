package ChaosAI.algorithm.beam;

import java.util.HashMap;
import java.util.LinkedList;

import core.game.StateObservation;
import ontology.Types;
import ChaosAI.utils.GameState;

/*
 * Created by Jakob Bruenker on 05.05.2016
 */

class BeamState extends GameState {
  private double heuristicValue = -1;
  private HashMap<Types.ACTIONS, Integer> children = new HashMap<>();
  private LinkedList<Types.ACTIONS> actionsToHere = new LinkedList<>();
  public BeamState(StateObservation so) {
    super(so);
  }

  public BeamState(GameState gs) {
    super(gs.getState());
  }

  public LinkedList<Types.ACTIONS> getATH() {
    return actionsToHere;
  }

  private boolean compare(Types.WINNER a, Types.WINNER b) {
    if (a == Types.WINNER.PLAYER_WINS) {
      return true;
    } else if (a == Types.WINNER.PLAYER_LOSES) {
      return false;
    } else if (b == Types.WINNER.PLAYER_WINS) {
      return false;
    } else {
      return true;
    }
  }

  public boolean compare(BeamState bs) {
    if (bs == null) {
      return true;
    } else if (getGameWinner() == bs.getGameWinner()) {
      return heuristic() > bs.heuristic();
    } else {
      return compare(getGameWinner(), bs.getGameWinner());
    }
  }

  public HashMap<Types.ACTIONS, Integer> getChildren() {
    return children;
  }

  public double heuristic() {
    if (heuristicValue == -1) {
      heuristicValue = getGameScore();
      if (getGameWinner() == Types.WINNER.PLAYER_WINS) {
        heuristicValue += 1000;
      } else if (getGameWinner() == Types.WINNER.PLAYER_LOSES) {
        heuristicValue -= 1000;
      }
    }
    return heuristicValue;
  }

  // TODO copy everything else
  public BeamState copy(){
        return new BeamState(getState().copy());
    }
}
