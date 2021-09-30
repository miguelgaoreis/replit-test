package ChaosAI.knowledgebase.locationbased;

import ChaosAI.knowledgebase.KnowledgeBaseConfig;
import core.game.Observation;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by user_ms on 18.05.16.
 */
public class GameBlock {

    // see ontology.Types for meaning of integers
    private HashSet<Integer> fieldTypesLearned;
    private ArrayList<Observation> observations;

    // -------------------------------------------------------------------------------------------------------
    // *** Constructor ***
    // -------------------------------------------------------------------------------------------------------
    GameBlock() {
        fieldTypesLearned = new HashSet<>(KnowledgeBaseConfig.ARRAYLIST_CAPACITY);
        observations = new ArrayList<>(KnowledgeBaseConfig.ARRAYLIST_CAPACITY);
    }

    // -------------------------------------------------------------------------------------------------------
    // *** getTypesCombined (combined from learned AND observed) ***
    // -------------------------------------------------------------------------------------------------------
    public ArrayList<Integer> getTypesCombined() {
        ArrayList<Integer> combined = new ArrayList<>(observations.size() + fieldTypesLearned.size());
        combined.addAll(fieldTypesLearned);
        for(Observation observation : observations)
            combined.add(observation.category);
        return combined;
    }

    // -------------------------------------------------------------------------------------------------------
    // *** getTypesLearned ***
    // -------------------------------------------------------------------------------------------------------
    public HashSet<Integer> getTypesLearned() {
        return fieldTypesLearned;
    }

    // -------------------------------------------------------------------------------------------------------
    // *** addTypeLearned ***
    // -------------------------------------------------------------------------------------------------------
    public void addTypeLearned(int fieldType) {
        this.fieldTypesLearned.add(fieldType);
    }

    // -------------------------------------------------------------------------------------------------------
    // *** removeTypeLearned ***
    // -------------------------------------------------------------------------------------------------------
    public void removeTypeLearned(int fieldType) {
        fieldTypesLearned.remove(Integer.valueOf(fieldType));
    }

    // -------------------------------------------------------------------------------------------------------
    // *** containsTypeCombined ***
    // -------------------------------------------------------------------------------------------------------
    public boolean containsTypeCombined(int fieldType) {
        return (containsTypeLearned(fieldType) || containsTypeObserved(fieldType));
    }

    // -------------------------------------------------------------------------------------------------------
    // *** containsTypeLearned ***
    // -------------------------------------------------------------------------------------------------------
    public boolean containsTypeLearned(int fieldType) {
        return fieldTypesLearned.contains(fieldType);
    }

    // -------------------------------------------------------------------------------------------------------
    // *** containsTypeObserved ***
    // -------------------------------------------------------------------------------------------------------
    public boolean containsTypeObserved(int fieldType) {
        for(Observation observation : observations)
            if(observation.category == fieldType)
                return true;
        return false;
    }

    // -------------------------------------------------------------------------------------------------------
    // *** clearLearned ***
    // -------------------------------------------------------------------------------------------------------
    public void clearLearned() {
        fieldTypesLearned.clear();
    }

    // -------------------------------------------------------------------------------------------------------
    // *** getSpriteIds ***
    // -------------------------------------------------------------------------------------------------------
    public ArrayList<Integer> getSpriteIds() {
        ArrayList<Integer> spriteIds = new ArrayList<>();
        for(Observation observation : observations) {
            spriteIds.add(observation.itype);
        }
        return spriteIds;
    }

    // -------------------------------------------------------------------------------------------------------
    // *** setObservations ***
    // -------------------------------------------------------------------------------------------------------
    public void setObservations(ArrayList<Observation> observations) {
        this.observations = observations;
    }

    // -------------------------------------------------------------------------------------------------------
    // *** getObservations ***
    // -------------------------------------------------------------------------------------------------------
    public ArrayList<Observation> getObservations() {
        return this.observations;
    }

}
