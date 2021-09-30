package tools;

import java.io.Serializable;

public class HumanMapStats implements Serializable {
    private String map;
    private int played;
    private double avg_score;
    private double avg_timesteps;
    private int won;

    public HumanMapStats(String _map){
        map = _map;
    }

    public void addGame(boolean _won, double score, double timestep){
        if(_won){
            won++;
        }
        avg_score = (avg_score*played + score) / played+1;
        avg_timesteps = (avg_timesteps*played + score) / played+1;
        played++;
    }

    public String getMap(){
        return map;
    }

    public double getPOS(){
        return (double)won/played;
    }

    public double getAvg_score(){
        return avg_score;
    }

    public double getAvg_timesteps(){
        return avg_timesteps;
    }
}
