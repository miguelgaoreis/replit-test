package ChaosAI.utils;

/**
 * Created by Chris on 18.05.2016.
 */
public class FieldTypes {

    // this is what we get from the observation
    public static final int TYPE_AVATAR         = 0;
    public static final int TYPE_RESOURCE       = 1;
    public static final int TYPE_PORTAL         = 2;
    public static final int TYPE_NPC            = 3;
    public static final int TYPE_STATIC         = 4;
    public static final int TYPE_FROMAVATAR     = 5;
    public static final int TYPE_MOVABLE        = 6;

    // this further refines the types as needed
    public static final int TYPE_FLOOR          = 7;    // Refinement for TYPE_STATIC
    public static final int TYPE_WALL           = 8;    // Refinement for TYPE_STATIC
    public static final int TYPE_ENEMY          = 9;    // Refinement for TYPE_NPC
    public static final int TYPE_STATIC_ENEMY   = 10;    // Refinement for TYPE_NPC
    public static final int TYPE_UNKNOWN        = 11;
}
