package tools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Ranker implements Serializable {

    private ArrayList<MapStats> mapsStats;
    private ArrayList<String> errors;
    public Ranker(ArrayList<String> _errors){
        mapsStats = new ArrayList<>();
        errors = _errors;
    }

    public void addMap(MapStats mapStat){
        mapsStats.add(mapStat);
    }

    public void rankByProbability(){
        for(String error : errors){
            if(error.equals("ContinuousOnCloseKeys")){
                List<MapStats> mapsStatsSorted =   mapsStats.stream()
                        .sorted(Comparator.comparing(MapStats::getAverage_POS_lossCRCK))
                        .collect(Collectors.toList());
                printRank(mapsStatsSorted, "Probability of Success", error);
            }
            else if(error.equals("Timing")){
                List<MapStats> mapsStatsSorted =   mapsStats.stream()
                        .sorted(Comparator.comparing(MapStats::getAverage_POS_lossTM))
                        .collect(Collectors.toList());
                printRank(mapsStatsSorted, "Probability of Success", error);
            }
            else if(error.equals("Mixed")){
                List<MapStats> mapsStatsSorted =   mapsStats.stream()
                        .sorted(Comparator.comparing(MapStats::getAverage_POS_lossMX))
                        .collect(Collectors.toList());
                printRank(mapsStatsSorted, "Probability of Success", error);
            }
        }
    }

    public void rankByAverageScore(){
        for(String error : errors) {
            if(error.equals("ContinuousOnCloseKeys")) {
                List<MapStats> mapsStatsSorted = mapsStats.stream()
                        .sorted(Comparator.comparing(MapStats::getAverage_scoreCRCK).reversed())
                        .collect(Collectors.toList());

                printRank(mapsStatsSorted, "Average Score", error);
            }
            else if(error.equals("Timing")) {
                List<MapStats> mapsStatsSorted = mapsStats.stream()
                        .sorted(Comparator.comparing(MapStats::getAverage_scoreTM).reversed())
                        .collect(Collectors.toList());

                printRank(mapsStatsSorted, "Average Score", error);
            }
            else if(error.equals("Mixed")) {
                List<MapStats> mapsStatsSorted = mapsStats.stream()
                        .sorted(Comparator.comparing(MapStats::getAverage_scoreMX).reversed())
                        .collect(Collectors.toList());

                printRank(mapsStatsSorted, "Average Score", error);
            }
        }
    }

    public void rankByAverageScoreLoss(){
        for(String error : errors) {
            if(error.equals("ContinuousOnCloseKeys")) {
                List<MapStats> mapsStatsSorted = mapsStats.stream()
                        .sorted(Comparator.comparing(MapStats::getAverage_scoreLossCRCK))
                        .collect(Collectors.toList());

                printRank(mapsStatsSorted, "Average Score Loss", error);
            }
            else if(error.equals("Timing")) {
                List<MapStats> mapsStatsSorted = mapsStats.stream()
                        .sorted(Comparator.comparing(MapStats::getAverage_scoreLossTM))
                        .collect(Collectors.toList());

                printRank(mapsStatsSorted, "Average Score Loss", error);
            }
            else if(error.equals("Mixed")) {
                List<MapStats> mapsStatsSorted = mapsStats.stream()
                        .sorted(Comparator.comparing(MapStats::getAverage_scoreLossMX))
                        .collect(Collectors.toList());

                printRank(mapsStatsSorted, "Average Score Loss", error);
            }
        }
    }

    public void printRank(List<MapStats> statsToPrint, String rankingType, String error){
        int rank = 1;
        int last_rank = 1;
        System.out.println("Ranking by " + rankingType + " with error " + error);
        int idx = errors.indexOf(error);
        if (rankingType.equals("Probability of Success")) {
            System.out.println("1º - " + statsToPrint.get(0).getMapName() + " with " + statsToPrint.get(0).getAverage_POS_lossByIdx(idx));
            for (int i = 1; i < statsToPrint.size(); i++) {
                if (statsToPrint.get(i - 1).getAverage_POS_lossByIdx(idx) == statsToPrint.get(i).getAverage_POS_lossByIdx(idx)) {
                    rank = last_rank;
                } else {
                    rank = i + 1;
                }
                System.out.println(rank + "º - " + statsToPrint.get(i).getMapName() + " with " + statsToPrint.get(i).getAverage_POS_lossByIdx(idx));
                last_rank = rank;
            }
        }
        else if (rankingType.equals("Average Score")) {
            System.out.println("1º - " + statsToPrint.get(0).getMapName() + " with " + statsToPrint.get(0).getAverage_scoreByIdx(idx));
            for (int i = 1; i < statsToPrint.size(); i++) {
                if (statsToPrint.get(i - 1).getAverage_scoreByIdx(idx) == statsToPrint.get(i).getAverage_scoreByIdx(idx)) {
                    rank = last_rank;
                } else {
                    rank = i + 1;
                }
                System.out.println(rank + "º - " + statsToPrint.get(i).getMapName() + " with " + statsToPrint.get(i).getAverage_scoreByIdx(idx));
                last_rank = rank;
            }
        }
        else if (rankingType.equals("Average Score Loss")) {
            System.out.println("1º - " + statsToPrint.get(0).getMapName() + " with " + statsToPrint.get(0).getAverage_scoreLossByIdx(idx));
            for (int i = 1; i < statsToPrint.size(); i++) {
                if (statsToPrint.get(i - 1).getAverage_scoreByIdx(idx) == statsToPrint.get(i).getAverage_scoreLossByIdx(idx)) {
                    rank = last_rank;
                } else {
                    rank = i + 1;
                }
                System.out.println(rank + "º - " + statsToPrint.get(i).getMapName() + " with " + statsToPrint.get(i).getAverage_scoreLossByIdx(idx));
                last_rank = rank;
            }
        }
    }
}
