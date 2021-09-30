package ChaosAI.heuristics;

import ChaosAI.utils.GameState;

/**
 * Created by Adrian on 10.05.2016.
 */
public abstract class HeuristicBase {

	public void updateFields(GameState pGameState){}

	public HeuristicBase getHeuristicByClass(Class<? extends HeuristicBase> reqClass){
		if(this.getClass() == reqClass)
			return this;
		return null;
	}
	
	// abstract Functions:
	/**
	 * evaluates the current State of game
	 * 
	 * @param pGameState : current State of game
	 */
	public abstract double evaluate(GameState pGameState);
}
