import desmoj.core.simulator.*;
/**
 * The Cashier entity encapsulates all data relevant for a cashier.
 */
public class Cashier extends Entity {
     private Client attendedClient;
    /**
     * Constructor of the cashier entity.
     *
     * @param owner the model this entity belongs to
     * @param name this Cashier's name
     * @param showInTrace flag to indicate if this entity shall produce output
     *                    for the trace
     */
    public Cashier(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);
        attendedClient=null;
    }

    public Client getAttendedClient(){
        return attendedClient;
    }

    public void setAttendedClient(Client client){
         attendedClient=client;
    }
}