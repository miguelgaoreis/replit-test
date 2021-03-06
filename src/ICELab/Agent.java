package ICELab;


import java.util.ArrayList;
import java.util.Random;

import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ICELab.BFS.BFS;
import ICELab.OpenLoopRLBiasMCTS.AnyTime;
import ICELab.OpenLoopRLBiasMCTS.Memory;
import ICELab.OpenLoopRLBiasMCTS.Node;
import ICELab.OpenLoopRLBiasMCTS.NodePool;
import ICELab.OpenLoopRLBiasMCTS.Utils;
import ICELab.OpenLoopRLBiasMCTS.TreeSearch.MCTS;
import ICELab.OpenLoopRLBiasMCTS.TreeSearch.RLBiasMCTS;
import ICELab.OpenLoopRLBiasMCTS.TreeSearch.TreeSearch;
import ICELab.OpenLoopRLBiasMCTS.TreeSearch.DBS;
import tools.ErrorGeneration;

//2016/5/18鐗�
//gvgai_2016Framework瀵惧繙
public class Agent extends AbstractPlayer{

	private JudgeGameType GameType;
	public static boolean USE_BFS = false;

	//use BFS
    public BFS best;
    public static int Max_GameTick;
	//
	//use OLRLBMCTS
    public static Random  random  = new Random();
    public static AnyTime anyTime = new AnyTime();
    public static Memory memory;
    public Node gameStart;
    public Node origin;
    public TreeSearch search;
    private boolean isInitializing = true;

    private ErrorGeneration errorGenerator;
    //
	public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer, String error, String replayer){
		
		GameInfo.init(so);
//      GameType = new JudgeGameType(so);
      if(GameInfo.isDetermin){
        	USE_BFS=true;
            //BFS初期化
            ArrayList<Types.ACTIONS> act = so.getAvailableActions();
            StateObservation temp = so.copy();
       	 	for(int j = 0; ;j++){
       	 		temp.advance(ACTIONS.ACTION_NIL);
       	 		if(temp.isGameOver()){
       	 			Max_GameTick = j;
       	 			break;
       	 		}
       	 	}
      	 	best = new BFS(so);
      	 	MCTSinit(so);
         	//search = new RLBiasMCTS(origin);
      	 	best.search(so,elapsedTimer);
       	 	Runtime.getRuntime().gc();
        }else{
//        	System.out.println("USE_OLRLBMCTS");
      	 	best = new BFS(so);
       	 	//OLRLBMCTS初期化
//       	 	System.out.print("begin init...");
       	 	MCTSinit(so);
    		anyTime.beginInit(elapsedTimer);
    		memory = new Memory(GameInfo.avatarType);
    		// choose our search algorithm
    		//search = new MCTS(origin);
    		search = new DBS(origin);
//    		search = new RLBiasMCTS(origin);
    		// fill search tree
    		search.search();

    		isInitializing = false;
         	search = new RLBiasMCTS(origin);
//         	System.out.println(" ...exit init");
        }
		this.errorGenerator = new ErrorGeneration(error ,so.getAvailableActions(true),replayer);
        //*/
    }
	public boolean isInitializing(){
		return isInitializing;
	}
	//MCTS初期設定
	public void MCTSinit(StateObservation so){
		Utils.initLogger();
		gameStart = NodePool.get();
		gameStart.state = so;
		gameStart.avatarPos = so.getAvatarPosition();
		gameStart.nVisits = 1;
		gameStart.depth = 0;
		origin = gameStart;

	}

    public Types.ACTIONS act(StateObservation so, ElapsedCpuTimer timer)
    {

    	if(USE_BFS){
			Types.ACTIONS action;
    		if (so.getGameTick() % 20 == 0) 
    		{
				Runtime.getRuntime().gc();
			}
    		action = best.Run(so,timer);
    		action = errorGenerator.generate_error(action);
    		return action;
    	}
    	else
    	{
    		anyTime.begin(timer);
            if (so.getGameTick() % 10 == 0)
             	Agent.memory.report();
            origin.state = so;
            origin.depth = 0;
            if (origin.prev != null) {
            	origin.prev.depth = -1;
            }
            // fill search tree
            search.search();

            // select action
            Node selected = origin.select();
            Types.ACTIONS action = selected.action;

            // release garbage & roll search tree
            selected.isDestroyable = false;
            origin.release();
            origin = selected;

            // roll search algorithm
            search.roll(origin);

            Utils.logger.info(action + "\n");

            action = this.errorGenerator.generate_error(action);

            return action;
            //*/
            //return ACTIONS.ACTION_NIL;
    	}
    }
    
}
