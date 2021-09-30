/**
 * Using a GA for local movement and a value map to direct the player across the level. 
 * The value map is created by evaluating each object type which results in a certain influence across the map.
 * Notable events, the time spent in one area or non deterministic movements influence the players behaviour or how it chooses its optimal action.
 * For deterministic games BFS is used.
 * 
 * Initialize and call functions of the needed controller type 
 * 
 * @author Florian Kirchgessner (Alias: Number27)
 * @version customGA3
 */

package Number27;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.ErrorGeneration;

public class Agent extends AbstractPlayer {
	private CustomAbstractPlayer controller;

	private ErrorGeneration errorGenerator;

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer, String error, String replayer) {
		this.errorGenerator = new ErrorGeneration(error ,stateObs.getAvailableActions(true),replayer);
    	if(stateObs.getNPCPositions() == null) {
    		controller = new BFS(stateObs, elapsedTimer);
    	}
    	else {
    		controller = new GA(stateObs, elapsedTimer);
    	}
    }
    

    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
    	if(controller.switchController() || stateObs.getNPCPositions() != null) {
    		if(controller instanceof BFS) {
    			controller = new GA(stateObs, elapsedTimer);
    		}
    	}

    	Types.ACTIONS action = controller.act(stateObs, elapsedTimer);

    	action = this.errorGenerator.generate_error(action);

    	return action;
    }
    
    
    @Override
    public void result(StateObservation stateObservation, ElapsedCpuTimer elapsedCpuTimer) {
    	controller.result(stateObservation, elapsedCpuTimer);
    }
}