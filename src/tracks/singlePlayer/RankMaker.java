package tracks.singlePlayer;

import tools.MapStats;
import tools.ObjectIO;
import tools.Ranker;
import tools.Utils;

import java.io.File;
import java.util.ArrayList;

public class RankMaker {

    public static void main(String[] args) {
        String spGamesCollection =  "examples/all_games_sp.csv";
        String[][] games = Utils.readGames(spGamesCollection);

        int games_with_stats[] = {/*0,*/43,113};

        ObjectIO objectIO = new ObjectIO();
        File f = new File("C:\\Users\\Mikel\\IdeaProjects\\GVGAI\\src\\tracks\\singlePlayer\\Files\\");
        String [] pathnames = f.list();


        for(int gameIdx : games_with_stats){
            Ranker ranker = null;
            ArrayList<MapStats> mapStats = new ArrayList<>();
            String gameName = games[gameIdx][1];
            String game = games[gameIdx][0];
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
    }
}
