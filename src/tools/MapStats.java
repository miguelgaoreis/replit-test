package tools;

import java.io.Serializable;
import java.util.ArrayList;

public class MapStats implements Serializable {
    private String gameName;
    private String mapName;
    private ArrayList<Double> wins;
    private double wins_normal;
    private ArrayList<Double> played;
    private double played_normal;
    private ArrayList<Double> average_score;
    private double average_score_normal;
    private ArrayList<Double> average_timestep;
    private double average_timestep_normal;
    private ArrayList<Double> average_score_loss;
    private ArrayList<Double> average_POS_loss;
    private ArrayList<Double> average_timestep_loss;
    private ArrayList<String> errors;

    public MapStats(String _gameName, String _mapName, ArrayList<String> _errors) {
        gameName = _gameName;
        int start_idx = _mapName.lastIndexOf('/')+1;
        int stop_idx = _mapName.indexOf('.');
        StringBuilder builder = new StringBuilder(_mapName);
        mapName = builder.substring(start_idx, stop_idx);
        wins_normal = 0;
        played_normal = 0;
        average_score_normal = 0.0;
        average_timestep_normal = 0;
        errors = _errors;
        wins = new ArrayList<>();
        played = new ArrayList<>();
        average_score = new ArrayList<>();
        average_timestep = new ArrayList<>();
        average_score_loss = new ArrayList<>();
        average_POS_loss = new ArrayList<>();
        average_timestep_loss = new ArrayList<>();
        for(String error: errors){
            wins.add(0.0);
            played.add(0.0);
            average_score.add(0.0);
            average_timestep.add(0.0);
            average_score_loss.add(0.0);
            average_POS_loss.add(0.0);
            average_timestep_loss.add(0.0);
        }
    }

    public void addGame(boolean _won, double _score,double _timestep, String error){
        int idx = errors.indexOf(error);
        played.set(idx, played.get(idx) + 1);
        if(_won){
            wins.set(idx, wins.get(idx) + 1);
        }
        average_score.set(idx, ((average_score.get(idx) * (played.get(idx)-1)) + _score) / played.get(idx));
        average_timestep.set(idx, ((average_timestep.get(idx) * (played.get(idx)-1)) + _timestep) / played.get(idx));
    }

    public void addNormalGame(boolean _won, double _score, double _timestep){
        played_normal++;
        if( _won ){
            wins_normal++;
        }
        average_score_normal = ((average_score_normal * (played_normal-1)) + _score) / played_normal;
        average_timestep_normal = ((average_timestep_normal * (played_normal-1)) + _timestep) / played_normal;
    }

    public void setFinalStats(){
        for(String error : errors){
            int idx = errors.indexOf(error);
            average_score_loss.set(idx, 100 - ((average_score.get(idx) * 100)/average_score_normal));
            average_timestep_loss.set(idx, 100 - ((average_timestep.get(idx) * 100)/average_timestep_normal));
            System.out.println("wins_n: " + wins_normal +"\tplayed_n: " + played_normal+"\twins: " + wins +"\tplayed: " + played );
            double temp1 = (wins_normal/played_normal*100);
            double temp2 = (wins.get(idx)/played.get(idx)*100);
            average_POS_loss.set(idx, temp1 - temp2);
        }
    }

    public double getAverage_scoreCRCK() {
        int idx = errors.indexOf("ContinuousOnCloseKeys");
        return  average_score.get(idx);
    }

    public double getAverage_scoreTM() {
        int idx = errors.indexOf("Timing");
        return  average_score.get(idx);
    }

    public double getAverage_scoreMX() {
        int idx = errors.indexOf("Mixed");
        return  average_score.get(idx);
    }

    public double getAverage_scoreAL() {
        int idx = errors.indexOf("ActionLimiter");
        return  average_score.get(idx);
    }

    public double getAverage_scoreMXAL() {
        int idx = errors.indexOf("MixedLimiter");
        return  average_score.get(idx);
    }

    public double getAverage_scoreByIdx(int idx) {
        return  average_score.get(idx);
    }

    public double getAverage_scoreLossCRCK() {
        int idx = errors.indexOf("ContinuousOnCloseKeys");
        return  average_score_loss.get(idx);
    }

    public double getAverage_scoreLossTM() {
        int idx = errors.indexOf("Timing");
        return  average_score_loss.get(idx);
    }

    public double getAverage_scoreLossMX() {
        int idx = errors.indexOf("Mixed");
        return  average_score_loss.get(idx);
    }

    public double getAverage_scoreLossAL() {
        int idx = errors.indexOf("ActionLimiter");
        return  average_score_loss.get(idx);
    }

    public double getAverage_scoreLossMXAL() {
        int idx = errors.indexOf("MixedLimiter");
        return  average_score_loss.get(idx);
    }

    public double getAverage_scoreLossByIdx(int idx) {
        return  average_score_loss.get(idx);
    }

    public double getAverage_POS_lossCRCK(){
        int idx = errors.indexOf("ContinuousOnCloseKeys");
        return average_POS_loss.get(idx);
    }

    public double getAverage_POS_lossTM(){
        int idx = errors.indexOf("Timing");
        return average_POS_loss.get(idx);
    }

    public double getAverage_POS_lossMX(){
        int idx = errors.indexOf("Mixed");
        return average_POS_loss.get(idx);
    }

    public double getAverage_POS_lossAL(){
        int idx = errors.indexOf("ActionLimiter");
        return average_POS_loss.get(idx);
    }

    public double getAverage_POS_lossMXAL(){
        int idx = errors.indexOf("MixedLimiter");
        return average_POS_loss.get(idx);
    }



    public double getAverage_POS_lossByIdx(int idx){
        return average_POS_loss.get(idx);
    }

    public String getMapName(){
        return mapName;
    }

    public ArrayList<String> getErrors(){
        return errors;
    }

}
