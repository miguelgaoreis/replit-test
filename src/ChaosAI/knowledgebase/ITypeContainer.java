package ChaosAI.knowledgebase;

import core.game.Observation;
import ontology.Types;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by blindguard on 09/06/16.
 */
public class ITypeContainer {
    private HashMap<Integer, Observation> map = new HashMap<>();
    private Integer itype;
    private Integer category;

    public ITypeContainer(Integer itype, Integer category) {
        this.itype = itype;
        this.category = category;
    }

    public void addObservation(Observation obs) {
        if(obs.itype == this.itype) {
            if(map.containsKey(obs.obsID)) {
                map.remove(obs.obsID);
            }
            map.put(obs.obsID, obs);
        }
    }

    public void updateObservation(Observation obs) {
        if(obs.itype == this.itype) {
            if(map.containsKey(obs.obsID)) {
                map.get(obs.obsID).update(obs.itype, obs.obsID, obs.position, obs.reference, obs.category);
            }
        }
    }

    public void clearContainer() {
        this.map.clear();
    }

    public Integer getObservationCount() {
        return this.map.size();
    }

    public Observation getObservation() {
        Collection<Observation> obsColl = this.map.values();
        return (Observation) obsColl.toArray()[0];
    }

    public Integer getCategory() {
        return this.category;
    }

    public Integer getItype() {
        return this.itype;
    }

    public String toString() {
        return "Cat: " + this.category + "   Cnt: " + this.map.size();
    }
}
