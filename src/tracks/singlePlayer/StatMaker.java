package tracks.singlePlayer;

import java.io.File;
import java.io.IOException;
import java.util.*;

import core.logging.Logger;
import tools.*;
import tracks.ArcadeMachine;

/**
 * Created with IntelliJ IDEA. User: Diego Date: 04/10/13 Time: 16:29 This is a
 * Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class StatMaker {

	private static HashMap<String, String> selected_controller;
	private static final String spGamesCollection =  "examples/all_games_sp.csv";
	private static final String[][] games = Utils.readGames(spGamesCollection);

	// Available tracks:
	private static final String sampleRandomController = "tracks.singlePlayer.simple.sampleRandom.Agent";
	private static final String doNothingController = "tracks.singlePlayer.simple.doNothing.Agent";
	private static final String sampleOneStepController = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";
	private static final String sampleFlatMCTSController = "tracks.singlePlayer.simple.greedyTreeSearch.Agent";

	private static final String sampleMCTSController = "tracks.singlePlayer.advanced.sampleMCTS.Agent";
	private static final String sampleRSController = "tracks.singlePlayer.advanced.sampleRS.Agent";
	private static final String sampleRHEAController = "tracks.singlePlayer.advanced.sampleRHEA.Agent";
	private static final String sampleOLETSController = "tracks.singlePlayer.advanced.olets.Agent";

	private static final String customYOLOBOT = "YOLOBOT.Agent";
	private static final String customasd592oldv = "asd592.Agent";
	private static final String customChaosAI = "ChaosAI.Agent";
	private static final String customNumber27 = "Number27.Agent";
	private static final String customICELab = "ICELab.Agent";
	private static final String customcombination = "combination.Agent";
	private static final String custommuzzle = "muzzle.Agent";
	private static final String customchildNorm = "childNorm.Agent";
	private static final String customsampleonesteplookahead = "sampleonesteplookahead.Agent";
	private static final String customadrienctx = "adrienctx.Agent";



    public static void main(String[] args) {

		//Game settings
		boolean visuals = true;
		int seed = new Random().nextInt();
		// Game and level to play
//		int gameIdx = 34;
//		int levelIdx = 0; // level names from 0 to 4 (game_lvlN.txt).

//		GAMES					0-Aliens, 1-Bomberman, 2-Cakybaky, 3-Eggomania, 4-Freeway, 5-RoadFighter, 6-Waves, 7-WildGunman , 8-X-Racer , 9-Bird
		int[] selected_games = {0	    , 9		     , 14        , 34         , 43       , 80           , 101    , 103          , 107       , 113};
		selected_controller = new HashMap<>();
		selected_controller.put(games[0][1], customasd592oldv);
		selected_controller.put(games[9][1], custommuzzle);
		selected_controller.put(games[14][1], customNumber27);
		selected_controller.put(games[34][1], customYOLOBOT);
		selected_controller.put(games[43][1], customadrienctx);
		selected_controller.put(games[80][1], customchildNorm);
		selected_controller.put(games[101][1], customYOLOBOT);
		selected_controller.put(games[103][1], customcombination);
		selected_controller.put(games[107][1], customsampleonesteplookahead);
		selected_controller.put(games[113][1], customcombination);


		int game_n = 4;
		int gameIdx = selected_games[game_n];
		int lvlIdx = 2;
		String gameName = games[gameIdx][1];
		String game = games[gameIdx][0];
		String controller = selected_controller.get(gameName);

		int n_maps = 1;
		int normal_playtrough = 50;
		int error_playtrough = 150;
		String level1;
		ArrayList<String> errors = new ArrayList<>();
//		errors.add("Nothing");
		errors.add("ContinuousOnCloseKeys");
		errors.add("Timing");
		errors.add("Mixed");
//		errors.add("ActionLimiter");
//		errors.add("MixedLimiter");

		int[] gameidxs = {selected_games[4]};
		getHumanPlaytroughs(gameidxs);
//		evaluate_human(gameIdx,lvlIdx,errors);
//		getStats(gameIdx,n_maps,normal_playtrough,error_playtrough,errors);
//		rankMaps(gameIdx);
//		generateActionFile(gameIdx,0,errors);
//		replayActionFile(gameIdx,0,errors);
//		int player_id = 2008600084;
//		analyze_player(player_id);
    }

	private static void getStats(int gameIdx, int n_maps, int normal_playtroughs, int error_playtroughs, ArrayList<String> errors){
		ObjectIO objectIO = new ObjectIO();
		String gameName = games[gameIdx][1];
		String game = games[gameIdx][0];
		String level = null;
		String controller = selected_controller.get(gameName);
		String recordActionsFile = null;
		boolean visuals = false;


		for (int j = 0; j<n_maps; j++ ){
			level = game.replace(gameName, gameName + "_lvl" + (j+8));
			System.out.println(level);
			MapStats mapStat = new MapStats(gameName, level, errors);
			int seed = new Random().nextInt();
			for( int i = 0; i<normal_playtroughs; i++) {
				System.out.print("Normal run nr " + i +"\t");
				double[] result = ArcadeMachine.runOneGame(game, level, visuals, controller, recordActionsFile, seed, 0, "Nothing", "null");
				mapStat.addNormalGame(result[0] == 1, result[1], result[2]);
			}
			for( int i = 0; i<error_playtroughs; i++) {
				for(String error : errors){
					System.out.print("Error run nr " + i +" with error " + error+"\t");
					double[] result = ArcadeMachine.runOneGame(game, level, visuals, controller, recordActionsFile, seed, 0, error, "null");
					mapStat.addGame(result[0] == 1,result[1],result[2],error);
				}
			}

			mapStat.setFinalStats();
			objectIO.WriteObjectToFile(mapStat,mapStat.getMapName());
		}
	}

	private static void rankMaps(int gameIdx){
		ObjectIO objectIO = new ObjectIO();
		File f = new File("src\\tracks\\singlePlayer\\Files\\");
		String [] pathnames = f.list();

		String gameName = games[gameIdx][1];
		String game = games[gameIdx][0];
		Ranker ranker = null;
		ArrayList<MapStats> mapStats = new ArrayList<>();

		for (String pathname : pathnames) {
			if (pathname.contains(gameName)){
				MapStats mapstat = (MapStats) objectIO.ReadObjectFromFile(pathname);
				if(ranker == null)
					ranker = new Ranker(mapstat.getErrors());
				ranker.addMap(mapstat);
			}
		}
		ranker.rankByProbability();
		ranker.rankByAverageScoreLoss();
	}

	private static void evaluate_human(int gameIdx,int levelIdx, ArrayList<String> errors){
		int seed = new Random().nextInt();
		seed = 23;
		int n_replays = 50;
		String game = games[gameIdx][0];
		String gameName = games[gameIdx][1];
		String level = game.replace(gameName, gameName + "_lvl2");
		String humanActionsFile =  "actions_" + gameName + "_lvl" + levelIdx +"human.txt";
		boolean visuals = false;
		String controller = selected_controller.get(gameName);

		ArcadeMachine.playOneGame(game, level, humanActionsFile, seed);

		String best_error = null;
		int dif_actions = 1000000000;
		ArrayList<String> human_list = new ArrayList<String>();
		try {
			Scanner s = new Scanner(new File(humanActionsFile));
			while (s.hasNext()) {
				human_list.add(s.next());
			}
			s.close();
		}catch (Exception e){e.printStackTrace();}


		for(String error: errors){
			int avg_count = 0;
			for(int j = 0; j<n_replays; j++) {
				int count = 0;
				File myObj = new File(humanActionsFile.replace("human", "Agent" + error));
				double[] result = ArcadeMachine.runOneGame(game, level, visuals, controller, null, seed, 0, error, humanActionsFile);

				ArrayList<String> list = new ArrayList<String>();
				try {
					Scanner s = new Scanner(new File(humanActionsFile.replace("human", "Agent" + error)));
					while (s.hasNext()) {
						list.add(s.next());
					}
					s.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				for (int i = 0; i < list.size(); i++) {
					if (!human_list.get(i + 1).equals(list.get(i))) {
						count++;
					}
				}
				System.out.println(error + ": " + count);
				avg_count = avg_count + count/n_replays;

				myObj.delete();

			}
			if(avg_count<dif_actions){
				dif_actions = avg_count;
				best_error = error;
			}
		}

		System.out.println("Best error is " + best_error + " with " + dif_actions + " different actions");
	}

	private static void generateActionFile(int gameIdx,int levelIdx, ArrayList<String> errors){
		int seed = new Random().nextInt();
		seed = 23;
		String game = games[gameIdx][0];
		String gameName = games[gameIdx][1];
		String level = game.replace(gameName, gameName + "_lvl1");
		String controller = selected_controller.get(gameName);
		boolean visuals = false;
		for(String error : errors){
			String actionsFile =  "actions_" + gameName + "_lvl" + levelIdx +"Agent"+ error+ ".txt";
			ArcadeMachine.runOneGame(game, level, visuals, controller, actionsFile, seed, 0, error,"null");
		}
	}

	private static void replayActionFile(int gameIdx,int levelIdx, ArrayList<String> errors){
		int seed = new Random().nextInt();
		seed = 23;
		String game = games[gameIdx][0];
		String gameName = games[gameIdx][1];
		String level = game.replace(gameName, gameName + "_lvl1");
		String controller = selected_controller.get(gameName);
		boolean visuals = true;
		for(String error : errors){
			String actionsFile =  "actions_" + gameName + "_lvl" + levelIdx +"Agent"+ error+ ".txt";
			ArcadeMachine.replayGame(game, level, visuals,actionsFile);
		}
	}

	public static void getHumanPlaytroughs(int[] gameIdxs){
		int nr_playtrough_p_map = 10;
		int n_maps = 5;
		Random rand_n = new Random();
		int player_n = rand_n.nextInt();
		String path = "src/tracks/Files/" + player_n;
		File f = new File("Results/player_" + player_n);
		//Creating a folder using mkdir() method
		boolean bool = f.mkdir();

		for(int gameIdx : gameIdxs){
			ArrayList<HumanMapStats> humanStats = new ArrayList<>();
			String game = games[gameIdx][0];
			String gameName = games[gameIdx][1];
			IntroPage introPage = new IntroPage(gameName);
			File myObj = new File("src/tracks/singlePlayer/Files/wait.txt");
			while (!myObj.exists()){
			}
			myObj.delete();
			String folderName = game.substring(0,game.lastIndexOf("/")) + "/";
			File folder = new File(folderName);
			File[] listOfFiles = folder.listFiles();
			ArrayList<String> gameLevels = new ArrayList<>();
			for (int i = 0; i < listOfFiles.length; i++) {
				if(listOfFiles[i].getName().contains(gameName+"_lvl")){
					gameLevels.add(listOfFiles[i].getName());
				}
			}

			Random rand = new Random();
			ArrayList<String> newList = new ArrayList<>();
			for (int i = 0; i < n_maps; i++) {
				// take a random index between 0 to size
				// of given List
				int randomIndex = rand.nextInt(gameLevels.size());

				// add element in temporary list
				newList.add(gameLevels.get(randomIndex).replace(".txt",""));

				// Remove selected element from original list
				gameLevels.remove(randomIndex);
			}
			System.out.println(newList);

			for(String levelName: newList){
				HumanMapStats humanStat = new HumanMapStats(levelName);
				String level = game.replace(gameName, levelName);
				for(int i = 0; i<nr_playtrough_p_map; i++){
					String humanActionsFile =  "Results/player_"+ player_n +"/actions_" + levelName +"humanP"+i+".txt";
					double[] result = ArcadeMachine.playOneGame(game, level, humanActionsFile, 3);
					humanStat.addGame(result[0] == 1,result[1],result[2]);
				}
				humanStats.add(humanStat);
			}
			ObjectIO objectIO = new ObjectIO();
			objectIO.ResultWriteObjectToFile(humanStats, "player_"+player_n+"/Human" + gameName);
			HashMap<String,ArrayList<String>> temp = new HashMap<>();
			temp.put(gameName,newList);
			FormPage2 formPage2 = new FormPage2(temp,player_n);
		}
	}

	public static void analyze_player(int player_id){
//		String game = games[gameIdx][0];
//		String gameName = games[gameIdx][1];
		File f = new File("src/tracks/singlePlayer/Files/player_" + player_id);

		// Populates the array with names of files and directories
		String[] pathnames = f.list();
		String formFile = "";
		String statFile = "";
		for (String filename : pathnames){
			if(filename.contains("Form")){
				formFile = filename;
			}
			else{
				statFile = filename;
			}
		}
		ObjectIO objectIO = new ObjectIO();
		ArrayList<HumanMapStats>  mapStats = (ArrayList<HumanMapStats>) objectIO.ReadObjectFromFile("player_"+player_id+"/" + statFile);
		HashMap<String, Integer> mapForm = (HashMap<String, Integer>) objectIO.ReadObjectFromFile("player_"+player_id+"/" + formFile);

		for (HumanMapStats humanMapStat : mapStats){
			System.out.println("Map: " + humanMapStat.getMap() +" POS: " + humanMapStat.getPOS());
		}
	}

}
