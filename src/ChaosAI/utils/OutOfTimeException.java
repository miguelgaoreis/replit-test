package ChaosAI.utils;

/**
 * Created by Chris on 10.05.2016.
 */
public class OutOfTimeException extends IllegalStateException {
    private final long remainingSeconds;
    public OutOfTimeException(long remainingSeconds){
        this.remainingSeconds = remainingSeconds;
    }
    @Override
    public String toString(){
        return "Time remaining: " + remainingSeconds;
    }
}
