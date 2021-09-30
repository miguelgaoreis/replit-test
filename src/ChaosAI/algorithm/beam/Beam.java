package ChaosAI.algorithm.beam;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import tools.ElapsedCpuTimer;
import ontology.Types;
import ChaosAI.algorithm.BaseAlgorithm;
import ChaosAI.knowledgebase.KnowledgeBase;
import ChaosAI.utils.GameState;
import ChaosAI.utils.StochasticException;
import ChaosAI.heuristics.HeuristicBase;

/**
 * Created by Jakob Bruenker on 05.05.2016
 */

// TODO: Could try to use heatmap to discourage searching in regions we've
// already been in. Also maybe sometimes try to search for longer, in order
// to not get stuck in things like bait (not sure what the criteria for
// searching longer are though)

public class Beam extends BaseAlgorithm {
  private final int BEAM_WIDTH = 2;

  private ArrayList<Integer> currSlice;
  private LinkedList<Integer> currChildren;
  private boolean continued = false;
  private HashMap<Integer,BeamState> hashMap;
  private Types.ACTIONS actionPerformed;
  private BeamState bestMove = null;
  private boolean nilLooses = false;

  @Override
  public void updateFields(GameState gs) {
    if (actionPerformed != null && currentState != null) {
      GameState test = currentState.copy();
      test.advance(actionPerformed);
      if (gs.hashCode() != test.hashCode() || gs.getNPCPositions() != null) {
        throw new StochasticException();
      }
    }
    this.currentState = gs.copy();
    evaluationHeuristic.updateFields(gs);
    GameState nilGs = gs.copy();
    nilGs.advance(Types.ACTIONS.ACTION_NIL);
    if (nilGs.getGameWinner() == Types.WINNER.PLAYER_LOSES) {
      nilLooses = true;
      if (bestMove == null || bestMove.getATH().isEmpty() || !bestMove.compare(new BeamState(currentState))) {
        for (Types.ACTIONS action : knownMoves) {
          GameState newGs = gs.copy();
          newGs.advance(action);
          if (newGs.getGameWinner() != Types.WINNER.PLAYER_LOSES) {
            bestMove = new BeamState(newGs);
            bestMove.getATH().add(action);
            break;
          }
        }
      }
    } else {
      nilLooses = false;
    }
  }

  public Beam(GameState gameState, HeuristicBase heuristic) {
    super(gameState, heuristic);
  }

  private boolean isHashed(BeamState bs) {
    return hashMap.containsKey(bs.hashCode());
  }

  private void hash(BeamState bs) {
    hashMap.put(bs.hashCode(), bs);
  }

  private void nextLayer() {
    boolean thresh = false; // XXX this is a bit of a hack
    for (int hash : currSlice) {
      if (thresh) {
        break;
      }
      BeamState bs = hashMap.get(hash);
      for (Types.ACTIONS action : knownMoves) {
        if (!bs.getChildren().containsKey(action)) {
          BeamState newBs = bs.copy();
          newBs.advance(action);
          newBs.getATH().addAll(bs.getATH());
          newBs.getATH().add(action);
          if (newBs.getATH().size() > 100) {
            thresh = true;
            break;
          }
          bs.getChildren().put(action, newBs.hashCode());
          if (!isHashed(newBs)) {
            hash(newBs);
            if (!newBs.isGameOver()) {
              currChildren.add(newBs.hashCode());
            }
            if (newBs.compare(bestMove)) {
              bestMove = newBs;
            }
          }
        }
      }
    }
    if (currChildren.size() == 0) {
      currSlice = new ArrayList<>();
      currSlice.add(currentState.hashCode());
      currChildren = new LinkedList<>();
      hashMap = new HashMap<>();
      hashMap.put(currentState.hashCode(), new BeamState(currentState));
      continued = true;
    } else {
      Collections.shuffle(currChildren);
      Collections.sort(currChildren, new HashHeurComparator());
      currSlice = new ArrayList<>();
      for (int i = 0; i < BEAM_WIDTH; i++) {
        if (currChildren.isEmpty()) {
          break;
        } else {
          currSlice.add(currChildren.pop());
        }
      }
      currChildren = new LinkedList<>();
    }
  }

  // Caution: this ignores non-existing hashes
  private class HashHeurComparator implements Comparator<Integer> {
    @Override
    public int compare(Integer hash1, Integer hash2) {
      double heur1 = evaluationHeuristic.evaluate(hashMap.get(hash1));
      double heur2 = evaluationHeuristic.evaluate(hashMap.get(hash2));
      if (heur1 > heur2) {
        return 1;
      } else if (heur1 == heur2) {
        return 0;
      } else {
        return -1;
      }
    }
  }

  public void computeOnce() {
    if (!continued) {
      currSlice = new ArrayList<>();
      currSlice.add(currentState.hashCode());
      currChildren = new LinkedList<>();
      hashMap = new HashMap<>();
      hashMap.put(currentState.hashCode(), new BeamState(currentState));
      continued = true;
    }
    nextLayer();
  }

  static int counter = 0;
  public Types.ACTIONS getNextMove() {
    if (bestMove != null && (nilLooses || (!bestMove.getATH().isEmpty() && bestMove.compare(new BeamState(currentState))))) {
      continued = false;
      Types.ACTIONS action = bestMove.getATH().pop();
      if (bestMove.getATH().isEmpty()) {
        bestMove = null;
      }
      actionPerformed = action;
      return action;
    } else {
      continued = true;
      actionPerformed = Types.ACTIONS.ACTION_NIL;
      return Types.ACTIONS.ACTION_NIL;
    }
  }
}
