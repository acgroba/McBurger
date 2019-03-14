import desmoj.core.simulator.*;
/**
 * The Client entity encapsulates all information associated with a client.
 */
public class Client extends Entity {
    /**
     * Constructor of the truck entity.
     *
     * @param owner the model this entity belongs to
     * @param name this client's name
     * @param showInTrace flag to indicate if this entity shall produce output
     *                    for the trace
     */
    public Client(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);
    }
}