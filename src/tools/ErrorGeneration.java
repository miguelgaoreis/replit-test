package tools;

import ontology.Types;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import org.apache.commons.math3.distribution.GeometricDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import java.util.concurrent.TimeUnit;



public class ErrorGeneration {
    private String errorType;
    private ArrayList<Types.ACTIONS> actions;
    private int step;
    private PoissonDistribution dist;
    private boolean inZone;
    private int stepsLeft;
    private Types.ACTIONS act_toDelay;
    private Types.ACTIONS prev_Act;
    private Types.ACTIONS action_delayed;
    private int prev_act_counter;
    private String replayer;
    private String agentProto;
    private PrintWriter out;

    public ErrorGeneration(String _errorType,ArrayList<Types.ACTIONS> _actions, String _replayer){
        errorType = _errorType;
        actions = _actions;
        step = 0;
        dist = new PoissonDistribution(0.5);
        inZone = false;
        stepsLeft = -1;
        prev_Act = Types.ACTIONS.ACTION_NIL;
        prev_act_counter++;
        replayer = _replayer;
        agentProto = _replayer.replace("human","Agent" + errorType);

        try{
            FileWriter fw = new FileWriter(replayer.replace("human","Agent" + errorType));
        }
        catch (Exception e){e.printStackTrace();}
    }

    public Types.ACTIONS generate_error(Types.ACTIONS _action) {
        step++;
        Types.ACTIONS new_action = _action;

        if(this.errorType.equals("ContinuousRandom")){
            if(Math.random()< 0.1){
                int randomAct = ThreadLocalRandom.current().nextInt(0, actions.size());
                new_action = actions.get(randomAct);
            }
        }
        else if(this.errorType.equals("ContinuousOnCloseKeys")){
            if(Math.random()< 0.03){
                ArrayList<Types.ACTIONS> closeActions = Types.ACTIONS.getCloseActions(_action);
                for (Iterator<Types.ACTIONS> iterator = closeActions.iterator(); iterator.hasNext();) {
                    Types.ACTIONS act = iterator.next();
                    if(!this.actions.contains(act)) {
                        iterator.remove();
                    }
                }
                Random rand;
                int randomAct = ThreadLocalRandom.current().nextInt(0, closeActions.size());
                new_action = closeActions.get(randomAct);
            }
        }
        else if(this.errorType.equals("Timing")){
            if( !inZone){
                if(!_action.equals(prev_Act)){
                    inZone = true;
                    stepsLeft = dist.sample();
                    act_toDelay = prev_Act;
                    action_delayed = _action;
//                    System.out.println("ENTERING ZONE, prev_act = " + prev_Act + "\t,chosen = " + _action + "\t, delay of = " + stepsLeft);
//                    try{Thread.sleep(4000);}catch(InterruptedException e){System.out.println(e);}
                }
            }

            if(inZone){
                if (stepsLeft == 0){
                    stepsLeft = -1;
                    inZone = false;
                    new_action = action_delayed;
//                    System.out.println("InZone: " + inZone + "\t stepsLeft: " + stepsLeft + "\taction:" + _action.toString() + "\t action performed: "+ new_action.toString());
//                    System.out.println("ZONE END----------------");
                }
                else{
                    stepsLeft--;
                    new_action = act_toDelay;
//                    System.out.println("InZone: " + inZone + "\t stepsLeft: " + stepsLeft + "\taction:" + _action.toString() + "\t action performed: "+ new_action.toString());
                }
            }
        }
        else if(this.errorType.equals("Mixed")){
            if( !inZone){
                if(!_action.equals(prev_Act)){
                    inZone = true;
                    stepsLeft = dist.sample();
                    act_toDelay = prev_Act;
                    action_delayed = _action;
//                    System.out.println("ENTERING ZONE, prev_act = " + prev_Act + "\t,chosen = " + _action + "\t, delay of = " + stepsLeft);
//                    try{Thread.sleep(4000);}catch(InterruptedException e){System.out.println(e);}
                }
            }

            if(inZone){
                if (stepsLeft == 0){
                    stepsLeft = -1;
                    inZone = false;
                    new_action = action_delayed;
//                    System.out.println("InZone: " + inZone + "\t stepsLeft: " + stepsLeft + "\taction:" + _action.toString() + "\t action performed: "+ new_action.toString());
//                    System.out.println("ZONE END----------------");
                }
                else{
                    stepsLeft--;
                    new_action = act_toDelay;
//                    System.out.println("InZone: " + inZone + "\t stepsLeft: " + stepsLeft + "\taction:" + _action.toString() + "\t action performed: "+ new_action.toString());
                }
            }
            if(Math.random()< 0.05){
                ArrayList<Types.ACTIONS> closeActions = Types.ACTIONS.getCloseActions(new_action);
                for (Iterator<Types.ACTIONS> iterator = closeActions.iterator(); iterator.hasNext();) {
                    Types.ACTIONS act = iterator.next();
                    if(!this.actions.contains(act)) {
                        iterator.remove();
                    }
                }
                int randomAct = ThreadLocalRandom.current().nextInt(0, closeActions.size());
                new_action = closeActions.get(randomAct);
            }
        }
        else if(this.errorType.equals("ActionLimiter")){
            if(_action.equals(prev_Act)){
                prev_act_counter++;
                new_action = _action;
            }
            else{
                if (prev_act_counter>=4){
                    new_action = _action;
                }
                else{
                    new_action = prev_Act;
                    prev_act_counter++;
                }
            }
        }
        else if(this.errorType.equals("MixedLimiter")){
            if( !inZone){
                if(!_action.equals(prev_Act)){
                    inZone = true;
                    stepsLeft = dist.sample();
                    act_toDelay = prev_Act;
                    action_delayed = _action;
//                    System.out.println("ENTERING ZONE, prev_act = " + prev_Act + "\t,chosen = " + _action + "\t, delay of = " + stepsLeft);
//                    try{Thread.sleep(4000);}catch(InterruptedException e){System.out.println(e);}
                }
            }

            if(inZone){
                if (stepsLeft == 0){
                    stepsLeft = -1;
                    inZone = false;
                    new_action = action_delayed;
//                    System.out.println("InZone: " + inZone + "\t stepsLeft: " + stepsLeft + "\taction:" + _action.toString() + "\t action performed: "+ new_action.toString());
//                    System.out.println("ZONE END----------------");
                }
                else{
                    stepsLeft--;
                    new_action = act_toDelay;
//                    System.out.println("InZone: " + inZone + "\t stepsLeft: " + stepsLeft + "\taction:" + _action.toString() + "\t action performed: "+ new_action.toString());
                }
            }
            if(Math.random()< 0.05){
                ArrayList<Types.ACTIONS> closeActions = Types.ACTIONS.getCloseActions(new_action);
                for (Iterator<Types.ACTIONS> iterator = closeActions.iterator(); iterator.hasNext();) {
                    Types.ACTIONS act = iterator.next();
                    if(!this.actions.contains(act)) {
                        iterator.remove();
                    }
                }
                int randomAct = ThreadLocalRandom.current().nextInt(0, closeActions.size());
                new_action = closeActions.get(randomAct);
            }
            if(new_action.equals(prev_Act)){
                prev_act_counter++;
            }
            else{
                if (prev_act_counter<4){
                    new_action = prev_Act;
                    prev_act_counter++;
                }
            }
        }
        if (replayer != "null"){

            try {
                String temp = new_action.toString()+"\n";
                Files.write(Paths.get(agentProto), temp.getBytes(), StandardOpenOption.APPEND);
            }catch (IOException e) {e.printStackTrace();}
                //exception handling left as an exercise for the reader
            try{
                 String line = Files.readAllLines(Paths.get(replayer)).get(step);
                 new_action = Types.ACTIONS.fromString(line);
            }catch (Exception e){ e.printStackTrace();}

        }

        prev_Act = new_action;
        return new_action;
    }
}
