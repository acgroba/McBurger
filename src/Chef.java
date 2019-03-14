import desmoj.core.simulator.*;
/**
 * The Chef entity encapsulates all data relevant for a chef.
 */
public class Chef extends Entity {

    /**
     * Constructor of the van carrier entity.
     *
     * @param owner the model this entity belongs to
     * @param name this Chef's name
     * @param showInTrace flag to indicate if this entity shall produce output
     *                    for the trace
     */
    public Chef(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);
    }
}