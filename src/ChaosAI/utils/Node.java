package ChaosAI.utils;

/**
 * Created by blindguard on 23/05/16.
 */
public interface Node {
    Node getParent();
    Node[] getChildren();
}
