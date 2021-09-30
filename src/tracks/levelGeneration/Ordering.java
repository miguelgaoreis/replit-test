package tracks.levelGeneration;

import tools.MapStats;
import tools.Ranker;
import tracks.ArcadeMachine;

import java.util.ArrayList;
import java.util.Random;

public class Ordering {


    public static void main(String[] args) {

        // Available Level Generators
        String randomLevelGenerator = "tracks.levelGeneration.randomLevelGenerator.LevelGenerator";
        String geneticGenerator = "tracks.levelGeneration.geneticLevelGenerator.LevelGenerator";
        String constructiveLevelGenerator = "tracks.levelGeneration.constructiveLevelGenerator.LevelGenerator";
        String architectLevelGenerator = "tracks.levelGeneration.architect.LevelGenerator";
        String luukgvgaiLevelGenerator = "tracks.levelGeneration.luukgvgai.LevelGenerator";

        // Available tracks:
        String sampleRandomController = "tracks.singlePlayer.simple.sampleRandom.Agent";
        String doNothingController = "tracks.singlePlayer.simple.doNothing.Agent";
        String sampleOneStepController = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";
        String sampleFlatMCTSController = "tracks.singlePlayer.simple.greedyTreeSearch.Agent";

        String sampleMCTSController = "tracks.singlePlayer.advanced.sampleMCTS.Agent";
        String sampleRSController = "tracks.singlePlayer.advanced.sampleRS.Agent";
        String sampleRHEAController = "tracks.singlePlayer.advanced.sampleRHEA.Agent";
        String sampleOLETSController = "tracks.singlePlayer.advanced.olets.Agent";

        String customYOLOBOT = "YOLOBOT.Agent";
        String customasd592 = "tracks.singlePlayer.custom.asd592.Agent";
        String customasd592oldv = "asd592.Agent";


        String gamesPath = "examples/gridphysics/";
        String physicsGamesPath = "examples/contphysics/";
        String generateLevelPath = gamesPath;


        String games[] = new String[] { "aliens", "angelsdemons", "assemblyline", "avoidgeorge", "bait", // 0-4
                "beltmanager", "blacksmoke", "boloadventures", "bomber", "bomberman", // 5-9
                "boulderchase", "boulderdash", "brainman", "butterflies", "cakybaky", // 10-14
                "camelRace", "catapults", "chainreaction", "chase", "chipschallenge", // 15-19
                "clusters", "colourescape", "chopper", "cookmepasta", "cops", // 20-24
                "crossfire", "defem", "defender", "digdug", "dungeon", // 25-29
                "eighthpassenger", "eggomania", "enemycitadel", "escape", "factorymanager", // 30-34
                "firecaster", "fireman", "firestorms", "freeway", "frogs", // 35-39
                "garbagecollector", "gymkhana", "hungrybirds", "iceandfire", "ikaruga", // 40-44
                "infection", "intersection", "islands", "jaws", "killBillVol1", // 45-49
                "labyrinth", "labyrinthdual", "lasers", "lasers2", "lemmings", // 50-54
                "missilecommand", "modality", "overload", "pacman", "painter", // 55-59
                "pokemon", "plants", "plaqueattack", "portals", "raceBet", // 60-64
                "raceBet2", "realportals", "realsokoban", "rivers", "roadfighter", // 65-69
                "roguelike", "run", "seaquest", "sheriff", "shipwreck", // 70-74
                "sokoban", "solarfox", "superman", "surround", "survivezombies", // 75-79
                "tercio", "thecitadel", "thesnowman", "waitforbreakfast", "watergame", // 80-84
                "waves", "whackamole", "wildgunman", "witnessprotection", "wrapsokoban", // 85-89
                "zelda", "zenpuzzle"}; //90, 91


        String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"
        // + levelIdx + "_" + seed + ".txt";
        // where to record the actions
        // executed. null if not to save.

        // Other settings
        int seed = new Random().nextInt();
        int gameIdx = 14;
        String recordLevelFile = generateLevelPath + games[gameIdx] + "_glvl.txt";
        String game = generateLevelPath + games[gameIdx] + ".txt";
        boolean visuals = true;
        String t_level = generateLevelPath +  games[gameIdx] + "_lvl0.txt";

        LevelGenMachine.runOneGeneratedLevel(game, visuals, customasd592oldv, recordActionsFile, t_level,  seed, false);

//		 2. This generates numberOfLevels levels.
        int numberOfLevels = 10;
        tracks.levelGeneration.randomLevelGenerator.LevelGenerator.includeBorders = true;



//		 for (int i = 0; i < numberOfLevels; i++) {
//		 	recordLevelFile = generateLevelPath +  games[gameIdx] + "_glvl" + i + ".txt";
//		 	LevelGenMachine.generateOneLevel(game, architectLevelGenerator, recordLevelFile);
//		}
//
//		 String level = generateLevelPath +  games[gameIdx] + "_glvl0.txt";
//
//        int n_times= 10 + 20;
//        ArrayList<MapStats> maps_stats = new ArrayList<MapStats>();
//        for (int j = 0; j<numberOfLevels; j++ ){
//            if (j>0){level = level.replace("_glvl" + (j-1),  "_glvl" + j);}
//
//            System.out.println(level);
//            MapStats mapStat = new MapStats(game, level,new ArrayList<String>());
//            seed = new Random().nextInt();
//            for( int i = 0; i<n_times; i++){
//                double result[] = LevelGenMachine.runOneGeneratedLevel(game, visuals, customasd592oldv, recordActionsFile, level,  seed, false);
//                if(i< 10){
//                    mapStat.addNormalGame(result[0] == 1,result[1]);
//                }
//                else{
//                    mapStat.addGame(result[0] == 1,result[1],"Nothing");
//                }
//            }
//            maps_stats.add(mapStat);
//        }

//        Ranker ranker = new Ranker();
//        ranker.rankByProbability();
//        ranker.rankByAverageScore();
//        ranker.rankByAverageScoreLoss();


    }
}
