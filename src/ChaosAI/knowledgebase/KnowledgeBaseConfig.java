package ChaosAI.knowledgebase;

/**
 * Created by user_ms on 18.05.16.
 */
public class KnowledgeBaseConfig {

    // General
    static final boolean DEBUG = true; // TODO remove before submitting

    // how many times to advance to detect stochastic effects
    static final int COUNT_ADVANCE_DETSTOCH = 12;
    static final int COUNT_ADVANCE_DEEP_DETSTOCH = 3;

    // detect walls dynamically while playout
    static final boolean DETECT_WALLS = true;

    // Set the intial capacity for the arraylists in GameBlock
    public static final int ARRAYLIST_CAPACITY = 10;

}
