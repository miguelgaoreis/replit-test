package ChaosAI.utils;

import tools.Vector2d;

/**
 * Created by Chris on 26.05.2016.
 */
public class Position {
    public final int x;
    public final int y;

    public Position(Vector2d vector,int blockSize){
        this.x = (int)vector.x/blockSize;
        this.y = (int)vector.y/blockSize;
    }
    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }
    @Override
    public String toString(){
        return "X: "+x+" Y: "+ y;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Position) {
            Position p = (Position) o;
            return p.x == this.x && p.y == this.y;
        } else {
            return false;
        }
    }
}
